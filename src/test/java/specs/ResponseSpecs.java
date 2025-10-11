package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;

public class ResponseSpecs {

    public static ResponseSpecification responseSpec(int expectedStatusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(expectedStatusCode)
                .log(ALL)
                .build();
    }

    public static ResponseSpecification successResponseSpec() {
        return responseSpec(200);
    }

    public static ResponseSpecification createdResponseSpec() {
        return responseSpec(201);
    }

    public static ResponseSpecification noContentResponseSpec() {
        return responseSpec(204);
    }

    public static ResponseSpecification notFoundResponseSpec() {
        return responseSpec(404);
    }

}