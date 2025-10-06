import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ReqResTests extends TestBase {

    @Test
    @DisplayName("Получение списка пользователей")
    void getListOfUsersTest() {
        given()
                .header("x-api-key", API_KEY)
                .contentType(ContentType.JSON)
                .log().uri()
                .when()
                .get("/users?page=2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("page", is(2))
                .body("per_page", is(6))
                .body("total", is(12))
                .body("total_pages", is(2))
                .body("data.size()", is(6))
                .body("data[0].id", is(7))
                .body("data[0].name", is("sand dollar"))
                .body("data[1].id", is(8))
                .body("data[1].name", is("chili pepper"))
                .body("data.id", hasItems(7, 8, 9, 10, 11, 12))
                .body("data.name", hasItems(
                        "sand dollar", "chili pepper", "blue iris",
                        "mimosa", "turquoise", "honeysuckle"
                ));
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getUserByIdTest() {
        given()
                .header("x-api-key", API_KEY)
                .contentType(ContentType.JSON)
                .log().uri()
                .when()
                .get("/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.id", is(2))
                .body("data.name", is("fuchsia rose"))
                .body("data.year", is(2001))
                .body("data.color", is("#C74375"))
                .body("data.pantone_value", is("17-2031"));
    }

    @Test
    @DisplayName("Создание пользователя")
    void createUser() {
        String expectedName = getUserNameFromJson();
        String expectedJob = getUserJobFromJson();

        given()
                .header("x-api-key", API_KEY)
                .body(userJson)
                .contentType(ContentType.JSON)
                .log().uri()
                .when()
                .post("/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", is(expectedName))
                .body("job", is(expectedJob))
                .body("id", notNullValue())
                .body("id", matchesPattern("\\d+"))
                .body("createdAt", notNullValue())
                .body("createdAt", matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"));
    }

    @Test
    @DisplayName("Обновление пользователя через метод PUT")
    void updateUserWithPutMethodTest() {
        String userId = createUserAndGetId();
        waitBetweenRequests();

        String updatedUserJson = generateUserWithNewJobJson();
        String expectedName = updatedUserJson.split("\"name\": \"")[1].split("\"")[0];
        String expectedJob = updatedUserJson.split("\"job\": \"")[1].split("\"")[0];

        given()
                .header("x-api-key", API_KEY)
                .body(updatedUserJson)
                .contentType(ContentType.JSON)
                .log().uri()
                .log().body()
                .when()
                .put("/users/" + userId)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("name", is(expectedName))
                .body("job", is(expectedJob))
                .body("createdAt", notNullValue())
                .body("createdAt", matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"));
    }

    @Test
    @DisplayName("Обновление пользователя через PATCH")
    void updateUserWithPatchMethodTest() {
        String userId = createUserAndGetId();
        waitBetweenRequests();

        String currentJob = getUserJobFromJson();
        String partialUpdateJson = "{\"job\": \"Senior " + currentJob + "\"}";

        given()
                .header("x-api-key", API_KEY)
                .body(partialUpdateJson)
                .contentType(ContentType.JSON)
                .log().uri()
                .log().body()
                .when()
                .patch("/users/" + userId)
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("job", is("Senior " + currentJob))
                .body("createdAt", notNullValue())
                .body("createdAt", matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUserTest() {
        String userId = createUserAndGetId();
        waitBetweenRequests();

        given()
                .header("x-api-key", API_KEY)
                .contentType(ContentType.JSON)
                .log().uri()
                .when()
                .delete("/users/" + userId)
                .then()
                .log().status()
                .statusCode(204);
    }
}