package goRest;

import goRest.pojo.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUserTests {

    private Integer userId;
    private String token;
    private RequestSpecification requestSpec;

    @BeforeClass
    public void init() {
        baseURI = "https://gorest.co.in/public-api/users/";
        token = "Bearer 55b19d86844d95532f80c9a2103e1a3af0aea11b96817e6a1861b0d6532eef47";
        requestSpec = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test()
    public void extractingListOfUsers() {
        List<User> userList = given()
                .when()
                .get()
                .then()
                .spec(getResponseSpecForStatus(200))
                .extract().response().jsonPath().getList("data", User.class);

        for (User user : userList) {
            System.out.println(user);
        }
    }

    @Test()
    public void extractingListOfUsersAsArray() {
        User[] userList = given()
                .when()
                .get()
                .then()
                .spec(getResponseSpecForStatus(200))
                .extract().response().jsonPath().getObject("data", User[].class);

        for (User user : userList) {
            System.out.println(user);
        }
    }

    @Test()
    public void extractingOneUser() {
        User user = given()
                .when()
                .get()
                .then()
                .spec(getResponseSpecForStatus(200))
                .extract().response().jsonPath().getObject("data[2]", User.class);

        System.out.println(user);
    }

    @Test
    public void creatingUser() {
        User user = new User();
        user.setEmail(randomEmail());
        user.setName("Techno");
        user.setGender("Male");
        user.setStatus("Active");

        userId = given()
                // specify Authorization header, body, Content-Type header
                .spec(requestSpec)
                .body(user)
                .when()
                .post()
                .then()
                .spec(getResponseSpecForStatus(201))
                .extract().response().jsonPath().getInt("data.id");
    }

    @Test(dependsOnMethods = "creatingUser")
    public void getUserById() {
        given()
                .when()
                .get(userId.toString())
                .then()
                .spec(getResponseSpecForStatus(200))
                .body("data.id", equalTo(userId))
        ;
    }

    @Test(dependsOnMethods = "creatingUser")
    public void updateUserById() {
        String updateString = "Techno Study";

        Map<String, String> body = new HashMap<>();
        body.put("name", updateString);

        given()
                // specify Authorization header, body, Content-Type header
                .spec(requestSpec)
                .body(body)
                .when()
                .put(userId.toString())
                .then()
                .spec(getResponseSpecForStatus(200))
                .body("data.name", equalTo(updateString))
        ;
    }

    @Test(dependsOnMethods = "creatingUser", priority = 1)
    public void deleteUserById() {
        given()
                // specify Authorization header, body, Content-Type header
                .spec(requestSpec)
                .when()
                .delete(userId.toString())
                .then()
                .spec(getResponseSpecForStatus(204))
        ;
    }

    @Test(dependsOnMethods = "deleteUserById")
    public void getUserByIdNegative() {
        given()
                .when()
                .get(userId.toString())
                .then()
                .spec(getResponseSpecForStatus(404))
        ;
    }

    private String randomEmail() {
        return RandomStringUtils.randomAlphabetic(8) + "@gmail.com";
    }

    private ResponseSpecification getResponseSpecForStatus(Integer status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectBody("code", equalTo(status))
                .build();
    }
}
