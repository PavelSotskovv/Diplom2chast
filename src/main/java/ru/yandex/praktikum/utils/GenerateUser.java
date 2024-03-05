package ru.yandex.praktikum.utils;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex.praktikum.entity.User;

public class GenerateUser {
    public static User getRandomUser() {
        String name = RandomStringUtils.randomAlphabetic(8);
        String email = name.toLowerCase() + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(8);

        Allure.addAttachment("Email : ", email);
        Allure.addAttachment("Password : ", password);
        Allure.addAttachment("Name : ", name);

        return new User(email, password, name);
    }
}
