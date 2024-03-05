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
import static org.hamcrest.Matchers.notNullValue;

@Epic("Login and logout user")
public class UserLoginLogoutTest {
    private static final String MESSAGE_LOGOUT = "Successful logout";
    private static final String MESSAGE_UNAUTHORIZED = "email or password are incorrect";
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
    @DisplayName("Вход пользователя с валидными данными в систему")
    public void userLoginByValidCredentials() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        boolean isLogin = response.extract().path("success");

        assertThat("Token is null", accessToken, notNullValue());
        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("User is login incorrect", isLogin, equalTo(true));
    }

    @Test
    @DisplayName("Выход пользователя с валидными данными из системы")
    public void userLogoutByValidCredentials() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        String refreshToken = response.extract().path("refreshToken");
        refreshToken = "{\"token\":\"" + refreshToken + "\"}";
        response = userClient.logoutUser(refreshToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogout = response.extract().path("success");

        assertThat("Token is null", refreshToken, notNullValue());
        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Message not equal", message, equalTo(MESSAGE_LOGOUT));
        assertThat("User is logout incorrect", isLogout, equalTo(true));
    }

    @Test
    @DisplayName("Вход в систему пользователя без email")
    public void userLoginByEmptyEmail() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setEmail(null);
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Token is null", accessToken, notNullValue());
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("User is login correct", isLogin, equalTo(false));
    }

    @Test
    @DisplayName("Вход в систему пользователя без пароля")
    public void userLoginByEmptyPassword() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setPassword(null);
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Token is null", accessToken, notNullValue());
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("User is login correct", isLogin, equalTo(false));
    }
}
