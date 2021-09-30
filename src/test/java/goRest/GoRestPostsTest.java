package goRest;

import goRest.Model.Post;
import goRest.Model.PostsBody;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestPostsTest {
    // Task 1 : https://gorest.co.in/public/v1/posts  API sinden dönen data bilgisini bir class yardımıyla
    // List ini alınız.

    @BeforeClass
    public void startUp()
    {
        baseURI ="https://gorest.co.in/public/v1";
    }

    @Test(enabled = false)
    public void getAllPosts() {
        List<Post> postList=
        given()

                .when()
                .get("/posts")

                .then()
                .extract().jsonPath().getList("data", Post.class)
        ;

        for (Post p: postList)
        {
            System.out.println("p = " + p);
        }
    }

    // Task 2 : https://gorest.co.in/public/v1/posts  API sinden sadece 1 kişiye ait postları listeletiniz.
    //  https://gorest.co.in/public/v1/users/87/posts

    @Test(enabled = false)
    public void getUserPosts()
    {
        List<Post> postUserList=
                given()

                        .when()
                        .get("/users/87/posts")

                        .then()
                        .extract().jsonPath().getList("data", Post.class)
                ;

        for (Post p: postUserList)
        {
            System.out.println("p = " + p);
        }

    }

    // Task 3 : https://gorest.co.in/public/v1/posts  API sinden dönen bütün bilgileri tek bir nesneye atınız
    @Test(enabled = false)
    public void getAllPostsAsObject() { // POJO
        PostsBody postsBody=
                given()

                        .when()
                        .get("/posts")

                        .then()
                        .extract().as(PostsBody.class)
                ;

        System.out.println("postsBody.getMeta().getPagination().getLinks().getCurrent() = " + postsBody.getMeta().getPagination().getLinks().getCurrent());
        System.out.println("postsBody.getData().get(3).getTitle() = " + postsBody.getData().get(3).getTitle());
    }

    // Task 4 : https://gorest.co.in/public/v1/posts  API sine 87 nolu usera ait bir post create ediniz.
    // gönderdiğiniz bilgilerin kayıt olduğunu kontrol ediniz.

    int postId=0;

    @Test
    public void createPost()
    {
        String postBody="icat çıkarmak iyidir";
        String postTitle="icat";

        Post post=new Post();
        post.setBody(postBody);
        post.setTitle(postTitle);

        postId=
        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .contentType(ContentType.JSON)
                .body(post)
                //.log().body()
                .when()
                .post("/users/87/posts")
                .then()
                .log().body()
                .body("data.title", equalTo(postTitle))
                .body("data.body", equalTo(postBody))
                .extract().jsonPath().getInt("data.id");
        ;

        System.out.println("postId = " + postId);
    }

    @Test(enabled = false)
    public void createPost2()
    {
        String postBody="icat çıkarmak iyidir";
        String postTitle="icat";

        Post post=new Post();
        post.setBody(postBody);
        post.setTitle(postTitle);
        post.setUser_id(87);

        postId=
                given()
                        .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                        .contentType(ContentType.JSON)
                        .body(post)
                        //.log().body()
                        .when()
                        .post("/posts")
                        .then()
                        .log().body()
                        .body("data.title", equalTo(postTitle))
                        .body("data.body", equalTo(postBody))
                        .extract().jsonPath().getInt("data.id");
        ;

        System.out.println("postId = " + postId);
    }

    // Task 5 : Create edilen Post ı get yaparak id sini kontrol ediniz.
    @Test(dependsOnMethods = "createPost")
    public void getPost()
    {
        given()
               .pathParam("postId", postId)

                .when()
                .get("posts/{postId}")

                .then()
                .statusCode(200)
                //.log().body()
                .body("data.id", equalTo(postId))
        ;
    }

    // Task 6 : Create edilen Post un body sini güncelleyerek, bilgiyi kontrol ediniz.
    @Test(dependsOnMethods = "createPost")
    public void postUpdate()
    {
        String body="Asistan candır";

        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .contentType(ContentType.JSON)
                .body("{\"body\":\"" + body + "\"}")
                .pathParam("postId", postId)
                .when()
                .put("posts/{postId}")

                .then()
                .log().body()
                .statusCode(200)
                .body("data.body", equalTo(body));
        ;
    }

   // Task 8 : Create edilen Post u siliniz. Status kodu kontorl ediniz 204

    @Test(dependsOnMethods = "postUpdate")
    public void postDelete()
    {
        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .pathParam("postId", postId)
                .when()
                .delete("posts/{postId}")
                .then()
                .statusCode(204)
        ;
    }

    // Task 6 : Silinen Post u negatif testini tekrar silmeye çalışarak yapınız.
    @Test(dependsOnMethods = "postDelete")
    public void postDeleteNegative()
    {
        given()
                .header("Authorization", "Bearer 36e95c8fd3e7eb89a65bad6edf4c0a62ddb758f9ed1e15bb98421fb0f1f3e57f")
                .pathParam("postId", postId)
                .when()
                .delete("posts/{postId}")
                .then()
                .statusCode(404)
        ;
    }













}
