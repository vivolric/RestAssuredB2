import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.Location;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    private RequestSpecification requestSpecification;
    private ResponseSpecification responseSpecification;

    @BeforeClass
    public void init() {
        baseURI = "http://api.zippopotam.us";

        requestSpecification = new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        responseSpecification = new ResponseSpecBuilder().
                expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectContentType(ContentType.JSON)
                .expectContentType(ContentType.JSON)
                .expectContentType(ContentType.JSON)
                .build();
    }


    @Test
    public void getTest() {
        given()
                .spec(requestSpecification)
                .when()
                .get("/us/90210") // action
                .then()
                .spec(responseSpecification)
        ;
    }

    @Test
    public void contentTypeTest() {
        given()
                .spec(requestSpecification)
                .when()
                .get("/us/90210")
                .then()
                .contentType(ContentType.JSON);
    }

    @Test
    public void chainingAssertionsTest() {
        given()
                // prior conditions
                .when()
                .get("/us/90210") // action
                .then() // checks comes after then()
                .spec(responseSpecification)
        ;
    }

    @Test
    public void logTest() {
        given()
                .log().all()  // print out everything about the request
                .when()
                .get("/us/90210")
                .then()
                .log().all() // print out everything about the response
        ;
    }

    @Test
    public void checkingResponseBody() {
        given()
                .when()
                .get("/us/07652")
                .then()
                .log().all()
                .body("places[0].state", equalTo("New Jersey"))
        ;
    }

    @Test
    public void checkingResponseBodyWithSpaceFields() {
        given()
                .when()
                .get("/us/07652")
                .then()
                .log().all()
                .body("places[0].'place name'", equalTo("Paramus"))
        ;
    }

    @Test
    public void checkingSizeOfArray() {
        given()
                .when()
                .get("/us/07652")
                .then()
                .log().body()
                .body("places", hasSize(1))
        ;
    }

    @Test
    public void chainingTest2() {
        given()
                .when()
                .get("/us/07652")
                .then()
                .log().body()
                .statusCode(200) // assertion checks
                .contentType(ContentType.JSON) // assertion checks
                .body("places", hasSize(1))
                .body("places[0].state", equalTo("New Jersey"))
                .body("places[0].'place name'", equalTo("Paramus"))
        ;
    }

    @Test
    public void usingPathParameters() {
        given()
                .log().uri()
                .pathParam("countryCode", "us")
                .pathParam("zipCode", "07652")
                .when()
                .get("/{countryCode}/{zipCode}")
                .then()
                .body("places", hasSize(1))
        ;
    }

    @Test
    public void usingQueryParameters() {
        //https://gorest.co.in/public-api/users?_format=json&page=3

        int page = 10;
        given()
                .log().uri()
                .param("_format", "json")
                .param("page", page)
                .when()
                .get("https://gorest.co.in/public-api/users")
                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(page))
        ;
    }

    @Test
    public void testingArrays() {
        given()
                .log().uri()
                .pathParam("countryCode", "tr")
                .pathParam("zipCode", "34840")
                .when()
                .get("/{countryCode}/{zipCode}")
                .then()
                .body("places", hasSize(2))
                .body("places.'place name'", hasItem("Altintepe Mah."))
        ;
    }

    @Test
    public void extractingArrays() {
        List<String> listOfPlaces = given()
                .log().uri()
                .pathParam("countryCode", "tr")
                .pathParam("zipCode", "34840")
                .when()
                .get("/{countryCode}/{zipCode}")
                .then()
                .extract().path("places.'place name'")
        ;

        System.out.println(listOfPlaces);
        Assert.assertTrue(listOfPlaces.contains("Altintepe Mah."));
    }

    @Test
    public void testingArraysNotEmpty() {
        given()
                .log().uri()
                .pathParam("countryCode", "tr")
                .pathParam("zipCode", "34840")
                .when()
                .get("/{countryCode}/{zipCode}")
                .then()
                .log().body()
                .body("places", not(empty()))
        ;
    }

    @Test
    public void extractingPojo() {
        Location location = given()
                .log().uri()
                .pathParam("countryCode", "tr")
                .pathParam("zipCode", "34840")
                .when()
                .get("/{countryCode}/{zipCode}")
                .then()
                .log().body()
                .extract().jsonPath().getObject("$", Location.class);

        System.out.println(location);
        System.out.println(location.getPostCode());
    }
}
