import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RegistrationTests extends TestBase {

    @Test
    @DisplayName("Успешная регистрация пользователя")
    void successfulRegistrationTest() {
        given()
                .header("x-api-key", API_KEY)
                .body(registerUser)
                .contentType(ContentType.JSON)
                .log().uri()

                .when()
                .post("/api/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("id", is(4))
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @DisplayName("Не успешная регистрация пользователя с отсутствием пароля")
    void unsuccessfulRegistrationTest() {
        given()
                .header("x-api-key", API_KEY)
                .body(userWithEmailOnly)
                .contentType(ContentType.JSON)
                .log().uri()

                .when()
                .post("/api/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
    }
}

