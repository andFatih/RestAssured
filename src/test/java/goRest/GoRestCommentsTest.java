package goRest;

import goRest.Model.Comment;
import goRest.Model.CommentsBody;
import goRest.Model.User;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.image.RescaleOp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestCommentsTest {
    // Task 1: https://gorest.co.in/public/v1/comments  Api sinden dönen verilerdeki data yı bir nesne yardımıyla
    //         List olarak alınız.


    @Test(enabled = false)
    public void getComments() {
        Response response =
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/comments")

                        .then()
                        //.log().body()
                        .extract().response();

        List<Comment> listComments1 = response.jsonPath().getList("data");
        List<Comment> listComments2 = response.jsonPath().getList("data", Comment.class);

        System.out.println("listComments1 = " + listComments1); // itemleri nesne olarak görmüyor
        System.out.println("listComments2 = " + listComments2);

//        for (Comment c:  listComments1) { // ClassCastException
//            System.out.println("l1 c = " + c); //c.toString()
//        }

        for (Comment c : listComments2) { // ClassCastException
            System.out.println("l2 c = " + c);
        }
    }

    // Bütün Comment lardaki emailleri bir list olarak alınız ve
    // içinde "acharya_rajinder@ankunding.biz" olduğunu doğrulayınız.

    @Test(enabled = false)
    public void getEmailList() {  // data[0].email  -> 1. email  , bütüm emailler için ise -> data.email
        List<String> emailList =
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/comments")

                        .then()
                        //.log().body()
                        .extract().path("data.email");  // Stringlerden oluşan bir List<String>
        ;

        System.out.println("emailList = " + emailList);
        for (String s : emailList) {
            System.out.println("s = " + s);
        }
        Assert.assertTrue(emailList.contains("acharya_rajinder@ankunding.biz"));
    }


    @Test(enabled = false)
    public void getEmailListRespons() {  // data[0].email  -> 1. email  , bütüm emailler için ise -> data.email
        Response response =
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/comments")

                        .then()
                        //.log().body()
                        .extract().response();
        ;

        List<String> emailList = response.path("data.email");
        List<String> emailList2 = response.jsonPath().getList("data.email", String.class);

        System.out.println("emailList = " + emailList2);
        for (String s : emailList2) {
            System.out.println("s = " + s);
        }
        Assert.assertTrue(emailList2.contains("acharya_rajinder@ankunding.biz"));
    }


    // Task 3 : https://gorest.co.in/public/v1/comments  Api sinden
    // dönen bütün verileri tek bir nesneye dönüştürünüz

    @Test(enabled = false)
    public void getCommentsPojo() {
        CommentsBody commentsBody =
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/comments")

                        .then()
                        //.log().body()
                        .extract().as(CommentsBody.class);
        ;
        // artık elimde response yani tüm datanın NESNE hali var

        System.out.println("commentsBody.getData().get(5).getEmail() = " + commentsBody.getData().get(5).getEmail());
        System.out.println("commentsBody.getMeta().getPagination().getLinks().getCurrent() = " + commentsBody.getMeta().getPagination().getLinks().getCurrent());

    }

    int commentId;

    // Task 4 : https://gorest.co.in/public/v1/comments  Api sine
    // 1 Comments Create ediniz.
    @Test
    public void CommentCreate() {
        Comment comment = new Comment();
        comment.setName("ismet temur");
        comment.setEmail("ismet@gmail.com");
        comment.setBody("Önce manuel, sonra atumation");

        commentId =
                given()
                        .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                        .body(comment)
                        .contentType(ContentType.JSON)

                        .when()
                        .post("https://gorest.co.in/public/v1/posts/68/comments")// 68 burada konu id ye karşılık gelen 68 i kullandık

                        .then()
                        .log().body()
                        .extract().jsonPath().getInt("data.id")
        ;

        System.out.println("commentId = " + commentId);
    }

    // Task 7 : Create edilen Comment ı get yapınız.
    @Test(dependsOnMethods = "CommentCreate")
    public void getComment() {
        given()
                .pathParam("commentId", commentId)
                .when()
                .get("https://gorest.co.in/public/v1/comments/{commentId}")

                .then()
                .log().body()
                .body("data.id", equalTo(commentId))
                .statusCode(200)
        ;
    }


    // Task 5 : Create edilen Comment ı n body kısmını güncelleyiniz.Sonrasında güncellemeyi kontrol ediniz.

    @Test(dependsOnMethods = "CommentCreate", priority = 1)
    public void CommentUpdate() {
        String postBody = "Önce manuel, sonra atumation";

        // String returnPostBody=
        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .body("{\"body\": \"" + postBody + "\"}")
                .contentType(ContentType.JSON)
                .pathParam("commentId", commentId)
                .log().body()
                .when()
                .put("https://gorest.co.in/public/v1/comments/{commentId}")

                .then()
                .log().body()
                //.extract().path("data.body")
                .body("data.body", equalTo(postBody))
        ;

        //Assert.assertTrue(returnPostBody.equalsIgnoreCase(postBody));
    }

    // Task 6 : Create edilen Comment ı siliniz. Status kodu kontorl ediniz 204
    @Test(dependsOnMethods = "CommentCreate", priority = 2)
    public void CommentDelete() {
        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .pathParam("commentId", commentId)

                .when()
                .delete("https://gorest.co.in/public/v1/comments/{commentId}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    // Task 6 : Silinen Comment ın negatif testini tekrar silmeye çalışarak yapınız.
    @Test(dependsOnMethods = "CommentDelete")
    public void CommentDeleteNegative() {
        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .pathParam("commentId", commentId)

                .when()
                .delete("https://gorest.co.in/public/v1/comments/{commentId}")

                .then()
                .log().body()
                .statusCode(404)
        ;
    }


}
