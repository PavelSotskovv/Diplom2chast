package ru.yandex.praktikum.user;

import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.UserClient;
import ru.yandex.praktikum.entity.User;
import ru.yandex.praktikum.utils.GenerateUser;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Update user")
public class UserUpdateTest {
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";
    private ValidatableResponse response;
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        userClient = new UserClient();
    }

    @After
    public void clearState() {
        userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
    }

    @Test
    @DisplayName("Обновление пользователя путем авторизации")
    public void updateUserByAuthorization() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        response = userClient.updateUserByAuthorization(GenerateUser.getRandomUser(), accessToken);
        int statusCode = response.extract().statusCode();
        boolean isUpdate = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("User is update incorrect", isUpdate, equalTo(true));
    }

    @Test
    @DisplayName("Обновление пользователя без авторизации")
    public void updateUserWithoutAuthorization() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.updateUserWithoutAuthorization(GenerateUser.getRandomUser());
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isUpdate = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("User is update correct", isUpdate, equalTo(false));
    }
}
