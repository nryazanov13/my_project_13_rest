package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import java.util.List;

import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static org.hamcrest.Matchers.*;

public class ResponseSpecs {

    public static ResponseSpecification createdResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .log(STATUS)
            .log(BODY)
            .build();

    // Специфические спецификации ТОЛЬКО с общими проверками структуры
    public static ResponseSpecification usersListResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody("page", notNullValue())
            .expectBody("per_page", notNullValue())
            .expectBody("total", notNullValue())
            .expectBody("total_pages", notNullValue())
            .expectBody("data", notNullValue())
            .expectBody("data", instanceOf(List.class))
            .log(STATUS)
            .log(BODY)
            .build();

    public static ResponseSpecification userByIdResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody("data", notNullValue())
            .expectBody("data.id", notNullValue())
            .expectBody("data.email", notNullValue())
            .expectBody("data.first_name", notNullValue())
            .expectBody("data.last_name", notNullValue())
            .log(STATUS)
            .log(BODY)
            .build();

    public static ResponseSpecification createUserResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .expectBody("name", notNullValue())
            .expectBody("job", notNullValue())
            .expectBody("id", notNullValue())
            .expectBody("id", matchesPattern("\\d+"))
            .expectBody("createdAt", notNullValue())
            .expectBody("createdAt", matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"))
            .log(STATUS)
            .log(BODY)
            .build();

    // Для PUT - полное обновление (проверяем все поля)
    public static ResponseSpecification updateUserPutResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody("name", notNullValue())
            .expectBody("job", notNullValue())
            .expectBody("updatedAt", notNullValue())
            .expectBody("updatedAt", matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"))
            .log(STATUS)
            .log(BODY)
            .build();

    // Для PATCH - проверяем только самое важное
    public static ResponseSpecification updateUserPatchResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody("updatedAt", notNullValue())
            .log(STATUS)
            .log(BODY)
            .build();

    public static ResponseSpecification deleteUserResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(204)
            .log(STATUS)
            .build();
}