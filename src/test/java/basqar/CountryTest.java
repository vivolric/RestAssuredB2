package basqar;

import basqar.model.Country;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CountryTest {

    private Cookies cookies;
    private String id;
    private String randomName;

    @BeforeClass
    public void init() {

        baseURI = "https://test.basqar.techno.study";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "daulet2030@gmail.com");
        credentials.put("password", "TechnoStudy123@");

        cookies = given()
                .contentType(ContentType.JSON)
                .body(credentials)
//                .body("{\"username\":\"daulet2030@gmail.com\",\"password\":\"TechnoStudy123@\",\"rememberMe\":false}")
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().response().detailedCookies();
    }

    @Test
    public void createTest() {
        Country body = new Country();
        randomName = RandomStringUtils.randomAlphabetic(8);
        body.setName(randomName);
        body.setCode(RandomStringUtils.randomAlphabetic(4));

        id = given()
                .cookies(cookies)
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .post("/school-service/api/countries")
                .then()
                .statusCode(201)
                .extract().jsonPath().getString("id");

        System.out.println(id);
        System.out.println(randomName);
    }

    @Test(dependsOnMethods = "createTest")
    public void searchTest() {
        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"" + randomName + "\"}")
                .when()
                .post("/school-service/api/countries/search")
                .then()
                .statusCode(200)
                .body(not(empty()))
                .body("name", hasItem(randomName))
        ;
    }

    @Test
    public void searchTestNegative() {
        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"" + RandomStringUtils.randomAlphabetic(8) + "\"}")
                .when()
                .post("/school-service/api/countries/search")
                .then()
                .statusCode(200)
                .body(equalTo("[]"))
        ;
    }

    @Test(dependsOnMethods = "createTest")
    public void createTestNegative() {
        Country body = new Country();
        body.setName(randomName);
        body.setCode(RandomStringUtils.randomAlphabetic(4));

        given()
                .cookies(cookies)
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .post("/school-service/api/countries")
                .then()
                .statusCode(400)
                .body("message", equalTo("The Country with Name \"" +
                        randomName + "\" already exists."));
    }

    @Test(dependsOnMethods = "createTest")
    public void updateTest() {
        Country body = new Country();
        body.setId(id);
        body.setName(RandomStringUtils.randomAlphabetic(8));
        body.setCode(RandomStringUtils.randomAlphabetic(4));

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/school-service/api/countries")
                .then()
                .statusCode(200)
                .body("name", equalTo(body.getName()))
                .body("code", equalTo(body.getCode()))
        ;
    }

    @Test(dependsOnMethods = "createTest", priority = 1)
    public void deleteTest() {
        given()
                .cookies(cookies)
                .when()
                .delete("/school-service/api/countries/" + id)
                .then()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteTest")
    public void updateTestNegative() {
        Country body = new Country();
        body.setId(id);
        body.setName(RandomStringUtils.randomAlphabetic(8));
        body.setCode(RandomStringUtils.randomAlphabetic(4));

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/school-service/api/countries")
                .then()
                .statusCode(404)
                .body("message", equalTo("Country not found"))
        ;
    }

    @Test(dependsOnMethods = "deleteTest")
    public void searchTestNegativeAfterDelete() {
        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"" + randomName + "\"}")
                .when()
                .post("/school-service/api/countries/search")
                .then()
                .statusCode(200)
                .body(equalTo("[]"))
        ;
    }

    @Test(dependsOnMethods = "deleteTest")
    public void deleteTestNegative() {
        given()
                .cookies(cookies)
                .when()
                .delete("/school-service/api/countries/" + id)
                .then()
                .statusCode(404)
                .body("message", equalTo("Country not found"))
        ;
    }
}
