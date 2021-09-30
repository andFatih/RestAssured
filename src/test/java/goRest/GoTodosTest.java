package goRest;


import goRest.Model.ToDo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoTodosTest {

    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public/v1";
    }

    // Task 1: https://gorest.co.in/public/v1/todos  Api sinden dönen verilerdeki
    //         en büyük id ye sahip todo nun id sini bulunuz.

    @Test(enabled = false)
    public void findBigIdOfTodos() {
        List<ToDo> todoList =
                given()

                        .when()
                        .get("/todos")

                        .then()
                        //.log().body()
                        .extract().jsonPath().getList("data", ToDo.class);

        System.out.println("todoList = " + todoList);

        int maxId = 0;
        for (int i = 0; i < todoList.size(); i++) {
            if (todoList.get(i).getId() > maxId) {
                maxId = todoList.get(i).getId();
            }
        }

        System.out.println("maxId = " + maxId);
    }

    // Task 2: https://gorest.co.in/public/v1/todos  Api sinden dönen verilerdeki
    //         en büyük id ye sahip todo nun id sini BÜTÜN PAGE leri dikkate alarak bulunuz.

    @Test(enabled = false)
    public void getBigestIdAllOfPageFor() {
        int totalPage = 2, maxID=0;

        for (int page = 1; page <= totalPage; page++) {

            Response response =
                    given()
                            .param("page", 2) // ?page=1
                            .when()
                            .get("/todos")

                            .then()
                            .log().body()
                            .extract().response();

            if (page == 1)
                totalPage = response.jsonPath().getInt("meta.pagination.pages");

            //sıradaki Page in datasını List olarak aldık
            List<ToDo> pageList = response.jsonPath().getList("data", ToDo.class);

            // elimizdeki en son maxID yi alarak bu pagedeki ID lerler karşılaştırıp en büyük ID yi almış olduk.
            for (int i = 0; i < pageList.size(); i++) {
                if (maxID < pageList.get(i).getId())
                    maxID = pageList.get(i).getId();
            }
        }
    }


    @Test(enabled = false)
    public void getBigestIdAllOfPage() {
        int totalPage = 0, page = 1, maxID = 0;

        do {
            Response response = // bir resposdan 2 tane extract yapacağım içi respons kullandım
                    given()
                            .param("page", page) // ?page=1
                            .when()
                            .get("/todos")

                            .then()
                            //.log().body()
                            .extract().response();

            if (page == 1) // kaç sayfa olduğunu bulduk
                totalPage = response.jsonPath().getInt("meta.pagination.pages");

            //sıradaki Page in datasını List olarak aldık
            List<ToDo> pageList = response.jsonPath().getList("data", ToDo.class);

            // elimizdeki en son maxID yi alarak bu pagedeki ID lerler karşılaştırıp en büyük ID yi almış olduk.
            for (int i = 0; i < pageList.size(); i++) {
                if (maxID < pageList.get(i).getId())
                    maxID = pageList.get(i).getId();
            }

            page++; // sonraki sayfaya geçiliyor
        } while (page <= totalPage);

        System.out.println("maxID = " + maxID);
    }


    // Task 3 : https://gorest.co.in/public/v1/todos  Api sinden
    // dönen bütün sayfalardaki bütün idleri tek bir List e atınız.

    @Test(enabled = false)
    public void getAllIdAllOfPage() {
        int totalPage = 0, page = 1;
        List<Integer> allToDoList = new ArrayList<>();

        do {
            Response response = // bir resposdan 2 tane extract yapacağım içi respons kullandım
                    given()
                            .param("page", page) // ?page=1
                            .when()
                            .get("/todos")

                            .then()
                            //.log().body()
                            .extract().response();

            if (page == 1) // kaç sayfa olduğunu bulduk
                totalPage = response.jsonPath().getInt("meta.pagination.pages");

            //sıradaki Page in datasını List olarak aldık
            List<Integer> pageList = response.jsonPath().getList("data.id");

            allToDoList.addAll(pageList);

            page++; // sonraki sayfaya geçiliyor
        } while (page <= totalPage);

        System.out.println("allToDoList = " + allToDoList);
    }

    int todoId;

    // Task 4 : https://gorest.co.in/public/v1/todos  Api sine
    // 1 todo Create ediniz.

    public String getNowDateToString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime date = LocalDateTime.now().plusMonths(3);
        return date.format(dateTimeFormatter);
    }

    @Test
     public void createTodo()
     {
         ToDo todo=new ToDo();
         todo.setStatus("pending");
         todo.setTitle("Bizim asistanımız bi tane");
         todo.setDue_on("2021-09-25");   //  todo.setDue_on(getNowDateToString());
         todo.setUser_id(7);

         todoId=
         given()
                 .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                 .contentType(ContentType.JSON)
                 .body(todo)
                 .when()
                 .post("/todos")

                 .then()
                 .log().body()
                 .statusCode(201)
                 .extract().jsonPath().getInt("data.id")
         ;

         System.out.println("todoId = " + todoId);
     }

    // Task 5 : Create edilen ToDo yu get yaparak id sini kontrol ediniz.

    @Test(dependsOnMethods = "createTodo")
    public void getToDo()
    {
        given()
                .pathParam("todoId", todoId)
                .when()
                .get("/todos/{todoId}")

                .then()
                .log().body()
                .statusCode(200)
                .body("data.id", equalTo(todoId))
        ;
    }

    // Task 6 : Create edilen ToDo u un status kısmını ("complated") güncelleyiniz.
    //  Sonrasında güncellemeyi kontrol ediniz.

    @Test(dependsOnMethods = "createTodo")
    public void updateToDo()
    {
        String status="completed";

        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .contentType(ContentType.JSON)
                .body("{\"status\":\"" + status + "\"}")
                .pathParam("todoId", todoId)
                .log().uri()
                .when()
                .put("/todos/{todoId}")
                .then()
                .statusCode(200)
                .log().body()
                .body("data.status",equalTo(status))
        ;

    }

    // Task 7 : Create edilen ToDo yu siliniz. Status kodu kontorl ediniz 204

    @Test(dependsOnMethods = "updateToDo")
    public void deleteToDo()
    {
        String status="completed";

        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .pathParam("todoId", todoId)
                .log().uri()
                .when()
                .delete("/todos/{todoId}")
                .then()
                .statusCode(204)
                .log().body()
        ;
    }

    @Test(dependsOnMethods = "deleteToDo")
    public void deleteNegativeToDo()
    {
        String status="completed";

        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .pathParam("todoId", todoId)
                .log().uri()
                .when()
                .delete("/todos/{todoId}")
                .then()
                .statusCode(404)
                .log().body()
        ;
    }

















}
