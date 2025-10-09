package tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import models.CreateUserResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static specs.RequestSpecs.createUserRequestSpec;
import static specs.ResponseSpecs.createdResponseSpec;

public class TestBase {

    protected Faker faker;
    protected String userJson;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in/api";
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
        CreateUserResponseModel response = given()
                .spec(createUserRequestSpec)
                .body(userJson)
                .when()
                .post()
                .then()
                .spec(createdResponseSpec)
                .extract().as(CreateUserResponseModel.class);

        return response.getId();
    }
}