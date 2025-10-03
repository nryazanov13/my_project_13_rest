import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    protected String loginUser = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\"}";
    protected String registerUser = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";
    protected String userWithPasswordOnly = "{\"password\": \"pistol\"}";
    protected String userWithEmailOnly = "{\"email\": \"sydney@fife\"}";

    protected static final String API_KEY = "reqres-free-v1";

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
    }
}