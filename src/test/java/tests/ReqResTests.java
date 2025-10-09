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
                given(usersListRequestSpec)
                        .queryParam("page", 2)
                        .when()
                        .get()
                        .then()
                        .spec(usersListResponseSpec)
                        .extract().as(UsersListResponseModel.class));

        step("Проверить данные ответа для списка пользователей", () -> {
            assertEquals(2, response.getPage(), "Номер страницы должен быть 2");
            assertEquals(6, response.getPer_page(), "Количество элементов на странице должно быть 6");
            assertEquals(12, response.getTotal(), "Общее количество должно быть 12");
            assertEquals(2, response.getTotal_pages(), "Общее количество страниц должно быть 2");
            assertEquals(6, response.getData().size(), "Размер массива данных должен быть 6");

            assertEquals(7, response.getData().get(0).getId(), "ID первого пользователя должен быть 7");
            assertEquals("Michael", response.getData().get(0).getFirst_name(), "Имя первого пользователя должно быть Michael");
            assertEquals("Lawson", response.getData().get(0).getLast_name(), "Фамилия первого пользователя должна быть Lawson");

            assertEquals(8, response.getData().get(1).getId(), "ID второго пользователя должен быть 8");
            assertEquals("Lindsay", response.getData().get(1).getFirst_name(), "Имя второго пользователя должно быть Lindsay");
            assertEquals("Ferguson", response.getData().get(1).getLast_name(), "Фамилия второго пользователя должна быть Ferguson");
        });
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getUserByIdTest() {
        UserResponseModel response = step("Получить пользователя по ID", () ->
                given(userByIdRequestSpec)
                        .pathParam("id", 2)
                        .when()
                        .get()
                        .then()
                        .spec(userByIdResponseSpec)
                        .extract().as(UserResponseModel.class));

        step("Проверить данные пользователя", () -> {
            assertEquals(2, response.getData().getId(), "ID пользователя должен быть 2");
            assertEquals("Janet", response.getData().getFirst_name(), "Имя пользователя должно быть Janet");
            assertEquals("Weaver", response.getData().getLast_name(), "Фамилия пользователя должна быть Weaver");
            assertEquals("janet.weaver@reqres.in", response.getData().getEmail(), "Email пользователя должен быть janet.weaver@reqres.in");
        });
    }

    @Test
    @DisplayName("Создание пользователя")
    void createUser() {
        // Создаем модель запроса
        CreateUserRequestModel request = new CreateUserRequestModel();
        request.setName(getUserNameFromJson());
        request.setJob(getUserJobFromJson());

        CreateUserResponseModel response = step("Создать пользователя", () ->
                given(createUserRequestSpec)
                        .body(request)
                        .when()
                        .post()
                        .then()
                        .spec(createUserResponseSpec)
                        .extract().as(CreateUserResponseModel.class));

        step("Проверить созданного пользователя", () -> {
            assertEquals(request.getName(), response.getName(), "Имя должно совпадать с отправленным");
            assertEquals(request.getJob(), response.getJob(), "Должность должна совпадать с отправленной");
            assertNotNull(response.getId(), "ID не должен быть null");
            assertTrue(response.getId().matches("\\d+"), "ID должен состоять из цифр");
            assertNotNull(response.getCreatedAt(), "Дата создания не должна быть null");
            assertTrue(response.getCreatedAt().matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"),
                    "Формат даты создания должен соответствовать ISO");
        });
    }

    @Test
    @DisplayName("Обновление пользователя через метод PUT")
    void updateUserWithPutMethodTest() {
        String userId = step("Создать пользователя для обновления", () ->
                createUserAndGetId());

        step("Подождать между запросами", () ->
                waitBetweenRequests());

        // Создаем модель запроса для обновления
        UpdateUserRequestModel request = new UpdateUserRequestModel();
        request.setName("Updated Name");
        request.setJob("Senior Developer");

        UpdateUserResponseModel response = step("Обновить пользователя через PUT", () ->
                given(updateUserRequestSpec)
                        .pathParam("id", userId)
                        .body(request)
                        .when()
                        .put()
                        .then()
                        .spec(updateUserPutResponseSpec)
                        .extract().as(UpdateUserResponseModel.class));

        step("Проверить обновленного пользователя", () -> {
            assertEquals(request.getName(), response.getName(), "Имя должно быть обновлено");
            assertEquals(request.getJob(), response.getJob(), "Должность должна быть обновлена");
            assertNotNull(response.getUpdatedAt(), "Дата обновления не должна быть null");
            assertTrue(response.getUpdatedAt().matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$"),
                    "Формат даты обновления должен соответствовать ISO");
        });
    }

    @Test
    @DisplayName("Обновление пользователя через PATCH")
    void updateUserWithPatchMethodTest() {
        String userId = step("Создать пользователя", () -> createUserAndGetId());
        waitBetweenRequests();

        // Просто отправляем новый job
        String newJob = "Senior " + getUserJobFromJson();
        String partialUpdateJson = "{\"job\": \"" + newJob + "\"}";

        UpdateUserResponseModel response = step("Обновить через PATCH", () ->
                given(updateUserRequestSpec)
                        .pathParam("id", userId)
                        .body(partialUpdateJson)
                        .when()
                        .patch()
                        .then()
                        .spec(updateUserPatchResponseSpec)
                        .extract().as(UpdateUserResponseModel.class));

        step("Проверить обновление", () -> {
            assertEquals(newJob, response.getJob());
            assertNotNull(response.getUpdatedAt());
        });
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUserTest() {
        String userId = step("Создать пользователя для удаления", () ->
                createUserAndGetId());

        step("Подождать между запросами", () ->
                waitBetweenRequests());

        step("Удалить пользователя", () ->
                given(deleteUserRequestSpec)
                        .pathParam("id", userId)
                        .when()
                        .delete()
                        .then()
                        .spec(deleteUserResponseSpec));

        step("Проверить что пользователь удален", () ->
                given(userByIdRequestSpec)
                        .pathParam("id", userId)
                        .when()
                        .get()
                        .then()
                        .statusCode(404));
    }
}