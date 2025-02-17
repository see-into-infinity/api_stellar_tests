package helpers.user;

import com.github.javafaker.Faker;
import models.user.User;

// Класс-генератор пользователей
public class UserGenerator {
    Faker faker = new Faker();

    public User getUserWithRandomCreds() {
        return new User( faker.internet().password(), faker.name().username(),faker.internet().emailAddress());
    }

    public User getUniqueUserWithValidCreds() {
        return new User("trytohackme",   "TestName", "test@email.ru");

    }

    public User getUserWithoutPasswordField() {
        return new User(faker.internet().emailAddress(), null, faker.name().username());
    }
}
