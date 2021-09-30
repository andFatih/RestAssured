package basqar;

import basqar.Model.Country;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CountryTest {

//    url : https://demo.mersys.io/auth/login
//    giden body :
//    {
//            "username": "richfield.edu",
//            "password": "Richfield2020!",
//            "rememberMe": true
//    }
//
//    POST : token create edileceği için

    Cookies cookies;

    @BeforeClass
    public void loginBasqar() {
        baseURI = "https://demo.mersys.io";

        Map<String, String> credential = new HashMap<>(); // login bilgileri
        credential.put("username", "richfield.edu");
        credential.put("password", "Richfield2020!");
        credential.put("rememberMe", "true");

        cookies = given()
                .body(credential)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                //.log().body()
                .extract().response().getDetailedCookies()
        ;

        System.out.println("cookies = " + cookies);
    }

    String randomGenName = RandomStringUtils.randomAlphabetic(6);
    String randomGenCode = RandomStringUtils.randomAlphabetic(3);
    String countryId;

    @Test
    public void createCountry() {

        Country country = new Country();
        country.setName(randomGenName);
        country.setCode(randomGenCode);

        countryId = given()
                .cookies(cookies) // gelen cookies (token bilgileri vs) i geri gönderdik
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .post("/school-service/api/countries")

                .then()
                .statusCode(201)
                .body("name", equalTo(randomGenName))
                //.log().body()
                .extract().jsonPath().getString("id")
        ;
    }

    @Test(dependsOnMethods = "createCountry")
    public void createCountryNegative() {

        Country country = new Country();
        country.setName(randomGenName);
        country.setCode(randomGenCode);

        given()
                .cookies(cookies) // gelen cookies (token bilgileri vs) i geri gönderdik
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .post("/school-service/api/countries")

                .then()
                .statusCode(400)
                .body("message", equalTo("The Country with Name \"" + randomGenName + "\" already exists."))
                .log().body()
        ;
    }

    @Test(dependsOnMethods = "createCountry")
    public void updateCountry() {
        String updRandGenName = RandomStringUtils.randomAlphabetic(6);

        Country country = new Country();
        country.setId(countryId);
        country.setName(updRandGenName);
        country.setCode(randomGenCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)
                .log().body()
                .when()
                .put("/school-service/api/countries")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(updRandGenName))
        ;
    }


    @Test(dependsOnMethods = "updateCountry")
    public void deleteById() {

        given()
                .cookies(cookies)
                .pathParam("countryId", countryId)
                .when()
                .delete("/school-service/api/countries/{countryId}")

                .then()
                .statusCode(200) // Jenkins için bilinçli olarak 200-201 yapıldı
                .log().body()
        ;
    }



}










