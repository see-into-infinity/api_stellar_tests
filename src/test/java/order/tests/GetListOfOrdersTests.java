package order.tests;


import decorator.LoggingExtension;
import helpers.order.OrderClient;
import helpers.order.OrderGenerator;
import helpers.user.UserClient;
import helpers.user.UserCreds;
import helpers.user.UserGenerator;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import models.order.ResponseGetOrder;
import models.user.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static listeners.CustomApiListener.withCustomTemplates;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
@ExtendWith(LoggingExtension.class)
public class GetListOfOrdersTests {
    private String accessToken;
    private OrderClient orderClient = new OrderClient();
    private OrderGenerator orderGenerator = new OrderGenerator();
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();
    private ValidatableResponse createUserResponse;
    private ValidatableResponse createOrderResponse;
    private ResponseGetOrder responseGetOrder;

    @BeforeAll
    public static void setUp(){
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), withCustomTemplates());
    }

    @Test
    @DisplayName("Check there is list of orders when user is authorized")
    public void checkUsersListOfOrdersWithAuth() {
        //Создаем пользователя
        User user = userGenerator.getUserWithRandomCreds();
        createUserResponse = userClient.createUser(user);

        //Авторизовываемся
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));
        accessToken = loginResponse.extract().path("accessToken");

        //Создаем заказ пользователя
        orderClient.createOrder(orderGenerator.getOrderWithCorrectIngredients(), accessToken);

        //Получаем заказ пользователя
        ValidatableResponse getUserOrders = orderClient.getOrders(accessToken);
        int actualGetOrderResponseStatusCode = getUserOrders.extract().statusCode();
        boolean isSuccess = getUserOrders.extract().path("success");


        Assertions.assertEquals(SC_OK, actualGetOrderResponseStatusCode);
        Assertions.assertEquals(isSuccess, true);


        //Достаем поля из ответа заказа пользователя и проверяем, что ингредиенты не пустые
        ResponseGetOrder responseGetOrder = orderClient.extractListOfUsersOrders(accessToken);
        Assertions.assertNotNull(responseGetOrder.getOrders());

    }

    @Test
    @DisplayName("Check there is no list of orders when user is not authorized")
    public void checkThereIsNoListOfOrdersWhenUserIsNotAuthorized() {
        accessToken = "wrongToken";

        ValidatableResponse getOrderListResponse = orderClient.getOrders(accessToken);
        int actualGetOrderResponseStatusCode = getOrderListResponse.extract().statusCode();
        String actualBodyMessage = getOrderListResponse.extract().path("message");

        Assertions.assertEquals(SC_UNAUTHORIZED, actualGetOrderResponseStatusCode);
        Assertions.assertEquals(orderClient.getErrorWhenReceiveListOfOrdersWithoutAuth(), actualBodyMessage);

    }

    @AfterEach
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
