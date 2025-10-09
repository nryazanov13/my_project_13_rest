package specs;

import io.restassured.specification.RequestSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;

public class RequestSpecs {

    // Константа вместо прямого обращения к TestBase (опционально)
    private static final String API_KEY = "reqres-free-v1";

    // Базовая спецификация для всех запросов
    public static RequestSpecification baseRequestSpec = with()
            .filter(withCustomTemplates())
            .log().uri()
            .log().headers()
            .contentType(JSON)
            .header("x-api-key", API_KEY);

    // Спецификации для конкретных эндпоинтов
    public static RequestSpecification usersListRequestSpec = with()
            .spec(baseRequestSpec)
            .basePath("/users");

    public static RequestSpecification userByIdRequestSpec = with()
            .spec(baseRequestSpec)
            .basePath("/users/{id}");

    public static RequestSpecification createUserRequestSpec = with()
            .spec(baseRequestSpec)
            .basePath("/users")
            .log().body();

    public static RequestSpecification updateUserRequestSpec = with()
            .spec(baseRequestSpec)
            .basePath("/users/{id}")
            .log().body();

    public static RequestSpecification deleteUserRequestSpec = with()
            .spec(baseRequestSpec)
            .basePath("/users/{id}");
}