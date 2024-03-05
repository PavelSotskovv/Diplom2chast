package ru.yandex.praktikum.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.config.BurgerConfig;
import ru.yandex.praktikum.entity.User;
import ru.yandex.praktikum.utils.EndPoints;

import static io.restassured.RestAssured.given;

public class UserClient extends BurgerConfig {
    @Step("Отправляем GET запрос в ручку /api/auth/user")
    public ValidatableResponse getUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .log().all()
                .get(EndPoints.USER_PATH + "user")
                .then()
                .log().all();
    }

    @Step("Отправляем POST запрос в ручку /api/auth/register")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .post(EndPoints.USER_PATH + "register")
                .then()
                .log().all();
    }

    @Step("Отправляем POST запрос в ручку /api/auth/login")
    public ValidatableResponse loginUser(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .body(user)
                .log().all()
                .post(EndPoints.USER_PATH + "login")
                .then()
                .log().all();
    }

    @Step("Отправляем POST запрос в  /api/auth/logout")
    public ValidatableResponse logoutUser(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .log().all()
                .post(EndPoints.USER_PATH + "logout")
                .then()
                .log().all();
    }

    @Step("Отправляем DELETE запрос в ручку /api/auth/user")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .log().all()
                .delete(EndPoints.USER_PATH + "user")
                .then()
                .log().all();
    }

    @Step("Отправляем PATCH запрос в ручку /api/auth/user")
    public ValidatableResponse updateUserByAuthorization(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(user)
                .log().all()
                .patch(EndPoints.USER_PATH + "user")
                .then()
                .log().all();
    }

    @Step("Отправляем PATCH запрос в ручку /api/auth/user")
    public ValidatableResponse updateUserWithoutAuthorization(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .patch(EndPoints.USER_PATH + "user")
                .then()
                .log().all();
    }
}
