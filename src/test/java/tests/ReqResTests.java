package tests;

import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
                        .queryParam("page", 2)
                        .when()
                        .get("users")
                        .then()
                        .spec(responseSpec(200))
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
                        .pathParam("id", 2)
                        .when()
                        .get("users/{id}")
                        .then()
                        .spec(responseSpec(200))
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
                        .body(request)
                        .when()
                        .post("users")
                        .then()
                        .spec(responseSpec(201))
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
                        .pathParam("id", userId)
                        .body(request)
                        .when()
                        .put("users/{id}")
                        .then()
                        .spec(responseSpec(200))
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
    @DisplayName("Обновление пользователя через PATCH - частичное обновление")
    void updateUserWithPatchMethodTest() {
        // 1. Создаем пользователя
        String userId = step("Создать пользователя", () ->
                createRandomUserAndGetId()); // Убрали лишние переменные

        waitBetweenRequests();

        // 2. PATCH - обновляем только job (используем Map чтобы не отправлять null)
        String updatedJob = "Senior Developer";
        Map<String, String> updateRequest = Map.of("job", updatedJob);

        UserResponseModel patchResponse = step("Обновить через PATCH", () ->
                given(requestWithBody)
                        .pathParam("id", userId)
                        .body(updateRequest)
                        .when()
                        .patch("users/{id}")
                        .then()
                        .spec(responseSpec(200))
                        .extract().as(UserResponseModel.class));

        // 3. ПРОВЕРКИ (только на основе PATCH ответа)
        step("Проверить частичное обновление", () -> {
            // Основная проверка - job обновился
            assertEquals(updatedJob, patchResponse.getJob(), "Job должен обновиться");

            // Проверяем что timestamp обновления установлен
            assertNotNull(patchResponse.getUpdatedAt(), "UpdatedAt должен быть установлен");

            // Проверяем формат timestamp
            assertTrue(patchResponse.getUpdatedAt().matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"),
                    "Формат updatedAt должен соответствовать ISO");

            // Для PATCH ответа проверяем что id и createdAt не вернулись (как в reqres.in)
            assertNull(patchResponse.getId(), "ID не должен возвращаться в PATCH ответе");
            assertNull(patchResponse.getCreatedAt(), "CreatedAt не должен возвращаться в PATCH ответе");

            // Дополнительная проверка: имя не должно быть в PATCH запросе
            assertFalse(updateRequest.containsKey("name"), "Name не должен отправляться в PATCH запросе");
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
                        .pathParam("id", userId)
                        .when()
                        .delete("users/{id}")
                        .then()
                        .spec(responseSpec(204)));

        step("Проверить что пользователь удален", () ->
                given(baseRequestSpec)
                        .pathParam("id", userId)
                        .when()
                        .get("users/{id}")
                        .then()
                        .spec(responseSpec(404)));
    }
}