package user.tests;


import decorator.LoggingExtension;
import helpers.user.UserClient;
import helpers.user.UserGenerator;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import models.user.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static listeners.CustomApiListener.withCustomTemplates;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

@ExtendWith(LoggingExtension.class)
public class ChangedUsersDataTests {
    private String accessToken;
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();

    @BeforeAll
    public static void setUp(){
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), withCustomTemplates());
    }

    @Test
    @DisplayName("Check users data can be changed with authorization")
    public void checkUsersDataCanBeChangedWithAuth() {
        User user = userGenerator.getUserWithRandomCreds();
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");

        ValidatableResponse responseWithChangedData = userClient.changeUsersCreds(userGenerator.getUserWithRandomCreds(), accessToken);
        int actualStatusCode = responseWithChangedData.extract().statusCode();
        boolean actualBodyMessage = responseWithChangedData.extract().path("success");

        Assertions.assertEquals(SC_OK, actualStatusCode);
        Assertions.assertEquals(actualBodyMessage, true);


    }

    @Test
    @DisplayName("Check users data cannot be changed without authorization")
    public void checkUsersDataCannotBeChangedWithoutAuth() {
        User user = userGenerator.getUserWithRandomCreds();
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");

        ValidatableResponse responseWithChangedData = userClient.changeUsersCreds(userGenerator.getUserWithRandomCreds(), "");
        int actualStatusResponse = responseWithChangedData.extract().statusCode();
        String actualErrorMessage = responseWithChangedData.extract().path("message");

        Assertions.assertEquals(SC_UNAUTHORIZED, actualStatusResponse);
        Assertions.assertEquals(actualErrorMessage, userClient.get401ErrorMessageWhenUserIsNotAuthorized());

    }

    @AfterEach
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
