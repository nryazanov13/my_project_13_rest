package tests;

import com.github.javafaker.Faker;
import models.UserRequestModel;
import models.UserResponseModel;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;
import static specs.RequestSpecs.requestWithBody;
import static specs.ResponseSpecs.responseSpec;

public class TestBase {

    protected Faker faker;

    @BeforeEach
    void initTestData() {
        faker = new Faker();
    }

    protected UserResponseModel createUser(UserRequestModel userRequest) {
        return given()
                .spec(requestWithBody)
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