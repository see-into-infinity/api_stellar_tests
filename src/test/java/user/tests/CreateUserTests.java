package user.tests;

import helpers.user.UserClient;
import helpers.user.UserGenerator;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import models.user.User;
import org.junit.jupiter.api.*;

import static listeners.CustomApiListener.withCustomTemplates;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

public class CreateUserTests {
    private String accessToken;
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();

    @BeforeAll
    static void setUp() {
        RestAssured.filters(withCustomTemplates());
    }

    @Test
    @DisplayName("Register a new user")
    public void registerNewUserTest() {
        ValidatableResponse response = userClient.createUser(userGenerator.getUserWithRandomCreds());
        int actualStatusCode = response.extract().statusCode();
        accessToken = response.extract().path("accessToken");

        Assertions.assertEquals(SC_OK, actualStatusCode);
        Assertions.assertEquals(response.extract().path("success"), true);
        Assertions.assertNotNull(accessToken);
    }

    @Test
    @DisplayName("Check there is 403 error and error message when trying to create a user that already exists")
    public void checkUserThatAlreadyExistsCannotBeCreated() {
        User user = userGenerator.getUniqueUserWithValidCreds();
        ValidatableResponse response = userClient.createUser(user);

        int actualStatusCode = response.extract().statusCode();
        String actualErrorMessage = response.extract().path("message");

        Assertions.assertEquals(SC_FORBIDDEN, actualStatusCode);
        Assertions.assertEquals(actualErrorMessage, userClient.get403ErrorMessageWhenUserAlreadyExists());

    }

    @Test
    @DisplayName("Check a user cannot be created without compulsory field")
    public void checkUserCannotBeCreatedWithoutCompulsoryField() {
        User user = userGenerator.getUserWithoutPasswordField();
        ValidatableResponse response = userClient.createUser(user);

        int actualStatusCode = response.extract().statusCode();
        String actualErrorMessage = response.extract().path("message");

        Assertions.assertEquals(SC_FORBIDDEN, actualStatusCode);
        Assertions.assertEquals(actualErrorMessage, userClient.get403ErrorMessageWhenUserIsCreatedWithoutCompulsoryField());
    }

    //Если пользователь был создан, удаляем его после каждого теста
    @AfterEach
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

}
