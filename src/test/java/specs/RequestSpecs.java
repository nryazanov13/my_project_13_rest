package specs;

import io.restassured.specification.RequestSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;

public class RequestSpecs {

    private static final String API_KEY = "reqres-free-v1";
    private static final String BASE_URI = "https://reqres.in";
    private static final String BASE_PATH = "/api";

    public static RequestSpecification baseRequestSpec = with()
            .baseUri(BASE_URI)
            .basePath(BASE_PATH)
            .filter(withCustomTemplates())
            .log().all()
            .contentType(JSON)
            .header("x-api-key", API_KEY);

    public static RequestSpecification requestWithBody = with()
            .spec(baseRequestSpec)
            .log().body();
}