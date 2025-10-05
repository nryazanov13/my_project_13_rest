import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    protected String user = "{\"name\": \"morpheus\", \"job\": \"leader\"}";
    protected String userWithNewJob = "{\"name\": \"morpheus\", \"job\": \"zion resident\"}";

    protected static final String API_KEY = "reqres-free-v1";

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }
}