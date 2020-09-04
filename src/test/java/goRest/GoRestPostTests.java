package goRest;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestPostTests {
    private int postId;

    @Test
    public void creatingPost() {
        int num = 899;
        postId = given()
                // specify Authorization header, body, Content-Type header
                .header("Authorization", "Bearer 55b19d86844d95532f80c9a2103e1a3af0aea11b96817e6a1861b0d6532eef47")
                .contentType(ContentType.JSON)
                .body("{           \"user_id\":" + num + ",\n" +
                        "            \"title\": \"Despasito\",\n" +
                        "            \"body\": \"LONG TIME NO SEE\"\n" +
                        "           \n" +
                        "        }")
                .when()
                .post("https://gorest.co.in/public-api/posts")
                .then()
                .statusCode(200)
                .body("code", equalTo(201))
                .extract().response().jsonPath().getInt("data.id");
    }

    @Test(dependsOnMethods = "creatingPost")
    public void getPostById() {
        given()
                .when()
                .get("https://gorest.co.in/public-api/posts/" + postId)
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data.id", equalTo(postId))
        ;
    }

    @Test(dependsOnMethods = "creatingPost")
    public void updatePostById() {
        String updateString = "Blablabla";
        given()
                // specify Authorization header, body, Content-Type header
                .header("Authorization", "Bearer 55b19d86844d95532f80c9a2103e1a3af0aea11b96817e6a1861b0d6532eef47")
                .contentType(ContentType.JSON)
                .body("{\"title\": \"" + updateString + "\"}")
                .when()
                .put("https://gorest.co.in/public-api/posts/" + postId)
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data.title", equalTo(updateString))
        ;
    }

    @Test(dependsOnMethods = "creatingPost", priority = 1)
    public void deletePostById() {
        given()
                // specify Authorization header, body, Content-Type header
                .header("Authorization", "Bearer 55b19d86844d95532f80c9a2103e1a3af0aea11b96817e6a1861b0d6532eef47")
                .when()
                .delete("https://gorest.co.in/public-api/posts/" + postId)
                .then()
                .statusCode(200)
                .body("code", equalTo(204))
        ;
    }

    @Test(dependsOnMethods = "deletePostById")
    public void getPostByIdNegative() {
        given()
                .when()
                .get("https://gorest.co.in/public-api/posts/" + postId)
                .then()
                .statusCode(200)
                .body("code", equalTo(404))
        ;
    }
}
