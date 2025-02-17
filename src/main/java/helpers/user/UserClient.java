package helpers.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.user.User;
import spec.Specification;

import static io.restassured.RestAssured.given;

public class UserClient extends Specification {
    public static final String REGISTER_URL = "api/auth/register";
    public static final String LOGIN_URL = "api/auth/login";
    public static final String UPDATE_PROFILE_URL = "api/auth/user";
    public static final String GET_PROFILE_URL = "api/auth/user";
    public static final String GET_ORDER_HISTORY_URL = "api/account/orders";

    public static final String USER_ALREADY_EXISTS_MESSAGE = "User already exists";
    public static final String USER_SHOULD_HAVE_REQUIRED_FIELDS_MESSAGE = "Email, password and name are required fields";
    public static final String USER_EMAIL_OR_PASSWORD_WRONG_MESSAGE = "email or password are incorrect";
    public static final String USER_SHOULD_BE_AUTHORIZED_MESSAGE = "You should be authorised";

    @Step("Create user")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(REGISTER_URL)
                .then();
    }

    @Step("Login user")
    public ValidatableResponse loginUser(UserCreds userCreds) {
        return given()
                .spec(getSpec())
                .body(userCreds)
                .post(LOGIN_URL)
                .then();
    }

    @Step("Get user profile ")
    public ValidatableResponse getUserProfile(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .get(GET_PROFILE_URL)
                .then();

    }

    @Step("Change user credentials")
    public ValidatableResponse changeUsersCreds(User user, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .spec(getSpec())
                .body(user)
                .patch(UPDATE_PROFILE_URL)
                .then();
    }

    @Step("Get user's order history")
    public ValidatableResponse getOrderHistory(User user, String accessToken) {
        return  given()
                .header("Authorization", accessToken)
                .spec(getSpec())
                .body(user)
                .patch(GET_ORDER_HISTORY_URL)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .spec(getSpec())
                .delete(UPDATE_PROFILE_URL)
                .then();
    }

    public String get403ErrorMessageWhenUserAlreadyExists() {
        return USER_ALREADY_EXISTS_MESSAGE;
    }

    public String get403ErrorMessageWhenUserIsCreatedWithoutCompulsoryField() {
        return USER_SHOULD_HAVE_REQUIRED_FIELDS_MESSAGE;
    }

    public String get401ErrorMessageWhenUserTriesToLoginWithWrongLoginAndPassword() {
        return USER_EMAIL_OR_PASSWORD_WRONG_MESSAGE;
    }

    public String get401ErrorMessageWhenUserIsNotAuthorized() {
        return USER_SHOULD_BE_AUTHORIZED_MESSAGE;
    }

}
