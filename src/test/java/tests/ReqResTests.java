package tests;

import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static specs.RequestSpecs.*;
import static specs.ResponseSpecs.*;

public class ReqResTests extends TestBase {

    @Test
    @DisplayName("Получение списка пользователей")
    void getListOfUsersTest() {
        UsersListResponseModel response = step("Получить список пользователей", () ->
                given(baseRequestSpec)
                        .basePath("/users")
                        .queryParam("page", 2)
                        .when()
                        .get()
                        .then()
                        .spec(successResponseSpec())
                        .extract().as(UsersListResponseModel.class));

        step("Проверить данные ответа для списка пользователей", () -> {
            assertEquals(2, response.getPage());
            assertEquals(6, response.getPer_page());
            assertEquals(12, response.getTotal());
            assertEquals(2, response.getTotal_pages());
            assertEquals(6, response.getData().size());

            assertEquals(7, response.getData().get(0).getId());
            assertEquals("Michael", response.getData().get(0).getFirst_name());
            assertEquals("Lawson", response.getData().get(0).getLast_name());

            assertEquals(8, response.getData().get(1).getId());
            assertEquals("Lindsay", response.getData().get(1).getFirst_name());
            assertEquals("Ferguson", response.getData().get(1).getLast_name());
        });
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getUserByIdTest() {
        SingleUserResponseModel response = step("Получить пользователя по ID", () ->
                given(baseRequestSpec)
                        .basePath("/users/{id}")
                        .pathParam("id", 2)
                        .when()
                        .get()
                        .then()
                        .spec(successResponseSpec())
                        .extract().as(SingleUserResponseModel.class));

        step("Проверить данные пользователя", () -> {
            assertEquals(2, response.getData().getId());
            assertEquals("Janet", response.getData().getFirst_name());
            assertEquals("Weaver", response.getData().getLast_name());
            assertEquals("janet.weaver@reqres.in", response.getData().getEmail());
        });
    }

    @Test
    @DisplayName("Создание пользователя")
    void createUserTest() {
        UserRequestModel request = generateRandomUser();

        UserResponseModel response = step("Создать пользователя", () ->
                given(requestWithBody)
                        .basePath("/users")
                        .body(request)
                        .when()
                        .post()
                        .then()
                        .spec(createdResponseSpec())
                        .extract().as(UserResponseModel.class));

        step("Проверить созданного пользователя", () -> {
            assertEquals(request.getName(), response.getName());
            assertEquals(request.getJob(), response.getJob());
            assertNotNull(response.getId());
            assertTrue(response.getId().matches("\\d+"));
            assertNotNull(response.getCreatedAt());
            assertTrue(response.getCreatedAt().matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"));
            assertNull(response.getUpdatedAt());
        });
    }

    @Test
    @DisplayName("Обновление пользователя через метод PUT")
    void updateUserWithPutMethodTest() {
        String userId = step("Создать пользователя для обновления", () ->
                createRandomUserAndGetId());

        waitBetweenRequests();

        // PUT - полное обновление (name И job)
        UserRequestModel request = new UserRequestModel("Updated Name", "Senior Developer");

        UserResponseModel response = step("Обновить пользователя через PUT", () ->
                given(requestWithBody)
                        .basePath("/users/{id}")
                        .pathParam("id", userId)
                        .body(request)
                        .when()
                        .put()
                        .then()
                        .spec(successResponseSpec())
                        .extract().as(UserResponseModel.class));

        step("Проверить обновленного пользователя", () -> {
            assertEquals(request.getName(), response.getName());  // Проверяем И имя
            assertEquals(request.getJob(), response.getJob());    // И должность
            assertNotNull(response.getUpdatedAt());
            assertTrue(response.getUpdatedAt().matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"));
            assertNull(response.getId());
            assertNull(response.getCreatedAt());
        });
    }

    @Test
    @DisplayName("Обновление пользователя через PATCH")
    void updateUserWithPatchMethodTest() {
        // Создаем пользователя и сохраняем его данные
        UserRequestModel originalUser = generateRandomUser();
        UserResponseModel createdUser = step("Создать пользователя", () ->
                createUser(originalUser));

        String userId = createdUser.getId();
        String originalName = createdUser.getName(); // Сохраняем исходное имя
        waitBetweenRequests();

        // PATCH - обновляем только job
        UserRequestModel updateRequest = new UserRequestModel();
        updateRequest.setJob("Senior Developer");

        UserResponseModel response = step("Обновить через PATCH", () ->
                given(requestWithBody)
                        .basePath("/users/{id}")
                        .pathParam("id", userId)
                        .body(updateRequest)
                        .when()
                        .patch()
                        .then()
                        .spec(successResponseSpec())
                        .extract().as(UserResponseModel.class));

        step("Проверить обновление", () -> {
            // Проверяем, что job обновился
            assertEquals("Senior Developer", response.getJob());
            // Проверяем, что имя осталось прежним
            assertEquals(originalName, response.getName());
            assertNotNull(response.getUpdatedAt());
        });
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUserTest() {
        String userId = step("Создать пользователя для удаления", () ->
                createRandomUserAndGetId());

        waitBetweenRequests();

        step("Удалить пользователя", () ->
                given(baseRequestSpec)
                        .basePath("/users/{id}")
                        .pathParam("id", userId)
                        .when()
                        .delete()
                        .then()
                        .spec(noContentResponseSpec()));

        step("Проверить что пользователь удален", () ->
                given(baseRequestSpec)
                        .basePath("/users/{id}")
                        .pathParam("id", userId)
                        .when()
                        .get()
                        .then()
                        .spec(notFoundResponseSpec()));
    }
}