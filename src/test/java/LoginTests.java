import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class LoginTests extends TestBase {

    @Test
    @DisplayName("Успешный вход пользователя")
    void successfulLoginTest() {
        given()
                .header("x-api-key", API_KEY)
                .body(loginUser)
                .contentType(ContentType.JSON)
                .log().uri()

                .when()
                .post("/api/login")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @DisplayName("Авторизация с заполненым полем почты без пароля")
    void loginWithEmailOnlyTest() {
        given()
                .header("x-api-key", API_KEY)
                .body(userWithEmailOnly)
                .contentType(ContentType.JSON)
                .log().uri()

                .when()
                .post("/api/login")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    @DisplayName("Авторизация с заполненым полем пароля без почты")
    void loginWithPasswordOnlyTest() {
        given()
                .header("x-api-key", API_KEY)
                .body(userWithPasswordOnly)
                .contentType(ContentType.JSON)
                .log().uri()

                .when()
                .post("/api/login")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing email or username"));
    }

    @Test
    @DisplayName("Логин пользователя без токена авторизации")
    void loginWithoutTokenTest() {
        given()
                .body(loginUser)
                .contentType(ContentType.JSON)
                .log().uri()

                .when()
                .post("/api/login")

                .then()
                .log().status()
                .log().body()
                .statusCode(401);
    }

    @Test
    @DisplayName("Логин пользователя без Content-Type")
    void loginWithoutContentTypeTest() {
        given()
                .body(loginUser)
                .header("x-api-key", API_KEY)
                .log().uri()

                .when()
                .post("/api/login")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing email or username"));
    }
}
