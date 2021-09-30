import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    public void test() {
        given()
                // hazırlık işlemlerini yapcağız

                .when()
                // link ve aksiyon işlemleri

                .then()
        // test ve extract işlemleri
        ;
    }

    @Test
    public void statusCodeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()   //  log.All() -> bütün respons u gösterir
                .statusCode(200)  // status kontrolü
        ;
    }

    @Test
    public void contentTypeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .contentType(ContentType.JSON)
        ;
    }

    @Test
    public void logTest() {
        given()
                .log().all()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
        ;
    }

    @Test
    public void checkStateInResponseBody() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("country", equalTo("United States")) // body.country == United States
                .statusCode(200)
        ;
    }

    @Test
    public void bodyJsonPathTest2() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].state", equalTo("California"))
                .statusCode(200)
        ;
    }

    @Test
    public void bodyJsonPathTestHasItem() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places.state", hasItem("California"))  // bütün state lerde aranan eleman var mı?
                .statusCode(200)
        ;

//        places[0].state -> listin 0 indexli elemanının state değerini verir, 1 değer
//        places.state ->    Bütün listteki state leri bir list olarak verir : California,California2   hasItem
//        List<String> list= {'California','California2'}
    }

    @Test
    public void bodyJsonPathTest4() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                // arasında bosluk olan key lerde keyin başına ve sonuna tek tırnak konur ('place name')
                .statusCode(200)
        ;
    }

    @Test
    public void bodyArrayHasSizeTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .body("places", hasSize(1)) // verilen path deki listin size kontrolü
                .log().body()
                .statusCode(200)
        ;
    }

    @Test
    public void combiningTest() {
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .body("places", hasSize(1))
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                // 1 den fazla assertion yapılabilir.
                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest() {
        String country = "us";
        String zipKod = "90210";

        given()
                .pathParam("country", country)
                .pathParam("zipKod", zipKod)
                .log().uri() //request linki

                .when()
                .get("http://api.zippopotam.us/{country}/{zipKod}")

                .then()
                .log().body()
                .body("places", hasSize(1))
        ;
    }

    @Test
    public void pathParamTest2() {
        String country = "us";

        for (int i = 90210; i < 90214; i++) {
            //String zipKod = i.toString();

            given()
                    .pathParam("country", country)
                    .pathParam("zipKod", i)
                    .log().uri() //request linki

                    .when()
                    .get("http://api.zippopotam.us/{country}/{zipKod}")

                    .then()
                    .log().body()
                    .body("places", hasSize(1))
            ;
        }

    }


    // https://gorest.co.in/public/v1/users?page=1
    @Test
    public void queryParamTest()
    {
        given()
                .param("page",1)
                .log().uri()

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(1));
        ;
    }

    @Test
    public void queryParamTestCoklu()
    {
        for(int page=1;page<=10;page++) {
            given()
                    .param("page", page)
                    .log().uri()

                    .when()
                    .get("https://gorest.co.in/public/v1/users")

                    .then()
                    .log().body()
                    .body("meta.pagination.page", equalTo(page));
        }
        ;
    }

    private ResponseSpecification responseSpecification;
    private RequestSpecification requestSpecification;

    @BeforeClass
    public void setup()
    {
        baseURI ="http://api.zippopotam.us";   // RestAssured kendi statik değişkeni tanımlı değer atanıyor.

        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setAccept(ContentType.JSON)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();
    }

    @Test
    public void bodyArrayHasSizeTest_requestSpecificationn() {
        given()
                .spec(requestSpecification)
                .when()
                .get("/us/90210")// url nin başında http yoksa baseUri deki değer otomatik geliyor.

                .then()
                .body("places", hasSize(1)) // verilen path deki listin size kontrolü
                .spec(responseSpecification)
        ;
    }

    @Test
    public void bodyArrayHasSizeTest_responseSpecification() {
        given()
                .log().uri()
                .when()
                .get("/us/90210")// url nin başında http yoksa baseUri deki değer otomatik geliyor.

                .then()
                .body("places", hasSize(1)) // verilen path deki listin size kontrolü
                .spec(responseSpecification)
        ;
    }

    @Test
    public void bodyArrayHasSizeTest_baseUriTest() {
        given()
                .log().uri()
                .when()
                .get("/us/90210")// url nin başında http yoksa baseUri deki değer otomatik geliyor.

                .then()
                .body("places", hasSize(1)) // verilen path deki listin size kontrolü
                .log().body()
                .statusCode(200)
        ;
    }


    // Json exract
    @Test
    public void extractingJsonPath()
    {
        String place_name = given()
                //.spec(requestSpecification)
                .when()
                .get("/us/90210")
                .then()
                .spec(responseSpecification)
                .extract().path("places[0].'place name'")  // extract metodu ile given ile başlayan satır, bir değer döndürür hale geldi
        ;

        System.out.println("place name = " + place_name);
    }


    @Test
    public void extractingJsonPathInt()
    {
        int limit=
        given()
                .param("page",1)
                //.log().uri()

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                //.log().body()
                .extract().path("meta.pagination.limit");
        ;

        System.out.println("limit = " + limit);
    }

    @Test
    public void extractingJsonPathIntList()
    {
        //    data[0].id -> 1.elemanın yani indexi 0 olanın id si
        //    data.id -> tum id ler demekdir list<int>

       List<Integer> idler=
                given()
                        .param("page",1)
                        //.log().uri()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .extract().path("data.id");
        ;

        System.out.println("idler = " + idler);
    }

    @Test
    public void extractingJsonPathStringList()
    {
        List<String> koyler= given()
                //.spec(requestSpecification)
                .when()
                .get("/tr/01000")
                .then()
                .spec(responseSpecification)
                .extract().path("places.'place name'")  // extract metodu ile given ile başlayan satır, bir değer döndürür hale geldi
                ;

        System.out.println("koyler = " + koyler);
        Assert.assertTrue(koyler.contains("Büyükdikili Köyü"));
    }

    @Test
    public void extractingJsonPOJO() // POJO : JSon Object i
    {
        Location location=
        given()
                .when()
                .get("/us/90210")

                .then()
                .extract().as(Location.class);
        ;

        System.out.println("location = " + location);
        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getPlaces() = " + location.getPlaces());
        System.out.println("location.getPlaces().get(0).getPlacename() = " + location.getPlaces().get(0).getPlacename());
    }




}






