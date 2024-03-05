package ru.yandex.praktikum.order;

import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.api.OrderClient;
import ru.yandex.praktikum.api.UserClient;
import ru.yandex.praktikum.entity.Order;
import ru.yandex.praktikum.entity.User;
import ru.yandex.praktikum.utils.GenerateUser;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Get order and ingredients")
public class OrderGetTest {
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised";
    private ValidatableResponse response;
    private User user;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        order = new Order();
        userClient = new UserClient();
        orderClient = new OrderClient();
        fillListIngredients();
    }

    @Test
    @DisplayName("Получить все возможные ингридиенты")
    public void getAllIngredients() {
        response = orderClient.getAllIngredients();
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Ingredients is get incorrect", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Получить все заказы в системе")
    public void getAllOrders() {
        response = orderClient.createOrderWithoutAuthorization(order);
        response = orderClient.getAllOrders();
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Orders is get incorrect", isGet, equalTo(true));

    }

    @Test
    @DisplayName("Получить заказы авторизованного пользователя")
    public void getOrderByAuthorizationUser() {
        response = userClient.createUser(user);
        String accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        response = orderClient.createOrderByAuthorization(order, accessToken);
        response = orderClient.getOrdersByAuthorization(accessToken);
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");
        response = userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Order is get incorrect", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Получить заказы неавторизованного пользователя")
    public void getOrderWithoutAuthorizationUser() {
        response = orderClient.createOrderWithoutAuthorization(order);
        response = orderClient.getOrdersWithoutAuthorization();
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isGet = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Order is get correct", isGet, equalTo(false));
    }

    private void fillListIngredients() {
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        ingredients.add(list.get(0));
        ingredients.add(list.get(5));
        ingredients.add(list.get(0));
    }
}
