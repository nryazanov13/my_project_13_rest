package tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import models.UserRequestModel;
import models.UserResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static specs.RequestSpecs.baseRequestSpec;
import static specs.ResponseSpecs.responseSpec;

public class TestBase {

    protected Faker faker;

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @BeforeEach
    void initTestData() {
        faker = new Faker();
    }

    protected UserResponseModel createUser(UserRequestModel userRequest) {
        return given()
                .spec(baseRequestSpec)
                .body(userRequest)
                .when()
                .post("users")
                .then()
                .spec(responseSpec(201))
                .extract().as(UserResponseModel.class);
    }

    protected UserRequestModel generateRandomUser() {
        return new UserRequestModel(
                faker.name().firstName(),
                faker.job().title()
        );
    }

    protected String createRandomUserAndGetId() {
        return createUser(generateRandomUser()).getId();
    }

    protected void waitBetweenRequests() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}