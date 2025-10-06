import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class TestBase {

    protected static final String API_KEY = "reqres-free-v1";
    protected Faker faker;
    protected String userJson;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void initTestData() {
        faker = new Faker();
        userJson = generateRandomUserJson();
    }

    protected String generateRandomUserJson() {
        String name = faker.name().firstName();
        String job = faker.job().title();
        return String.format("{\"name\": \"%s\", \"job\": \"%s\"}", name, job);
    }

    protected String generateUserWithNewJobJson() {
        String name = faker.name().firstName();
        String job = faker.job().position();
        return String.format("{\"name\": \"%s\", \"job\": \"%s\"}", name, job);
    }

    protected String getUserNameFromJson() {
        return userJson.split("\"name\": \"")[1].split("\"")[0];
    }

    protected String getUserJobFromJson() {
        return userJson.split("\"job\": \"")[1].split("\"")[0];
    }

    protected void waitBetweenRequests() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected String createUserAndGetId() {
        return given()
                .header("x-api-key", API_KEY)
                .body(userJson)
                .contentType(ContentType.JSON)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }
}