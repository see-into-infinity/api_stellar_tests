package user.tests;


import helpers.user.UserClient;
import helpers.user.UserCreds;
import helpers.user.UserGenerator;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import models.user.User;
import org.junit.jupiter.api.*;

import static listeners.CustomApiListener.withCustomTemplates;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class LoginUserTests {
    private String accessToken;
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();
    private User user = new User();
    private ValidatableResponse createUserResponse;

    @BeforeAll
    public static void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), withCustomTemplates());
    }

    @BeforeEach
    public void createUser() {
        user = userGenerator.getUserWithRandomCreds();
        createUserResponse = userClient.createUser(user);
    }


    @Test
    @DisplayName("Check a user with valid credentials can login")
    public void checkUserCanLoginWithValidCredentials() {
        //Выполняем логин новым пользователем
        ValidatableResponse response = userClient.loginUser(UserCreds.from(user));

        int actualStatusCode = response.extract().statusCode();
        accessToken = response.extract().path("accessToken");
        boolean actualBodyMessage = response.extract().path("success");

        Assertions.assertEquals(SC_OK, actualStatusCode);
        Assertions.assertEquals(actualBodyMessage, true);

    }


    @Test
    @DisplayName("Check a user with wrong login and password cannot login")
    public void checkUserWithWrongLoginAndPasswordCannotLogin() {
        accessToken = createUserResponse.extract().path("accessToken");

        user.setName("WrongName");
        user.setPassword("WrongPass");

        ValidatableResponse responseToLogin = userClient.loginUser(UserCreds.from(user));

        int actualStatusCode = responseToLogin.extract().statusCode();
        String actualErrorMessage = responseToLogin.extract().path("message");

        Assertions.assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        Assertions.assertEquals(userClient.get401ErrorMessageWhenUserTriesToLoginWithWrongLoginAndPassword(), actualErrorMessage);

    }

    @AfterEach
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
