package order.tests;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import helpers.order.OrderClient;
import helpers.order.OrderGenerator;
import helpers.user.UserClient;
import helpers.user.UserCreds;
import helpers.user.UserGenerator;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import listeners.CustomApiListener;
import models.order.ResponseGetOrder;
import models.user.User;
import org.junit.jupiter.api.*;

import static listeners.CustomApiListener.withCustomTemplates;
import static org.apache.http.HttpStatus.*;

public class CreateOrderTests {
    private String accessToken;
    private OrderClient orderClient = new OrderClient();
    private OrderGenerator orderGenerator = new OrderGenerator();
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();
    private User user;
    private ValidatableResponse createUserResponse;

    @BeforeAll
    public static void setUp(){
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), withCustomTemplates());
    }

    //Создаем нового пользователя перед каждым тестом
    @BeforeEach
    public void createUser() {
         user = userGenerator.getUserWithRandomCreds();
         createUserResponse = userClient.createUser(user);
    }
    
    @Test
    @DisplayName("Check order creation with auth and ingredients")
    public void checkOrderCreationWithAuth() {
        //Авторизуемся пользователем
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));
        accessToken = loginResponse.extract().path("accessToken");

        //Создаем заказ
        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithCorrectIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();


        //Проверяем, что заказ создался успешно
        Assertions.assertEquals(SC_OK,actualStatusCode);
        Assertions.assertEquals(createOrderResponse.extract().path("success"), true);

        //Выполняем проверку полей ответа на метод получения заказа
        ResponseGetOrder responseGetOrder = orderClient.extractListOfUsersOrders(accessToken);

//        Assertions.assertEquals(responseGetOrder.getOrders(), );

        

    }

    @Test
    @DisplayName("Check order can be created without auth and with ingredients")
    public void checkOrderCannotBeCreatedWithoutAuth() {
        accessToken = "wrongToken";

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithCorrectIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assertions.assertEquals(SC_OK, actualStatusCode);
        Assertions.assertEquals(createOrderResponse.extract().path("success"), true);
    }

    @Test
    @DisplayName("Check order cannot be created created with authorization and without ingredients")
    public void checkOrderCannotBeCreatedWithoutIngredients() {
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));

        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithNoIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();
        String actualBodyMessage = createOrderResponse.extract().path("message");


        Assertions.assertEquals(SC_BAD_REQUEST, actualStatusCode);
        Assertions.assertEquals(orderClient.getErrorWhenIngredientsIdsMustBeProvided(), actualBodyMessage);

    }
    @Test
    @DisplayName("Check order cannot be created created without authorization and without ingredients")
    public void checkOrderCannotBeCreatedWithoutAuthAndIngredients() {
        accessToken = "wrongToken";

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithNoIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assertions.assertEquals(SC_BAD_REQUEST, actualStatusCode);
        Assertions.assertEquals(orderClient.getErrorWhenIngredientsIdsMustBeProvided(), createOrderResponse.extract().path("message"));

    }

    @Test
    @DisplayName("Check order cannot be created with wrong ingredients hash")
    public void checkOrderCannotBeCreatedWithWrongIngredientHash() {
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));

        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithIncorrectIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assertions.assertEquals(SC_INTERNAL_SERVER_ERROR, actualStatusCode);

    }

    @AfterEach
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
