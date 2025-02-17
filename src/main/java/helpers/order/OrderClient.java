package helpers.order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.order.Order;
import models.order.ResponseGetOrder;
import spec.Specification;
import static io.restassured.RestAssured.given;

public class OrderClient extends Specification {
    public final static String CREATE_ORDER = "api/orders";
    public final static String GET_ORDERS = "api/orders";

    public final static String INGREDIENT_ERROR_MESSAGE = "Ingredient ids must be provided";
    public final static String GET_LIST_OF_ORDERS_WITHOUT_AUTH_ERROR_MESSAGE = "You should be authorised";

    @Step("Create order")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(order)
                .post(CREATE_ORDER)
                .then();
    }

    @Step("Get list of orders")
    public ValidatableResponse getOrders(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .get(GET_ORDERS)
                .then();
    }

    @Step("Extract list of user's orders")
    public ResponseGetOrder extractListOfUsersOrders(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .get(GET_ORDERS)
                .then()
                .extract().as(ResponseGetOrder.class);


    }


    public String getErrorWhenIngredientsIdsMustBeProvided() {
        return INGREDIENT_ERROR_MESSAGE;
    }

    public String getErrorWhenReceiveListOfOrdersWithoutAuth() {
        return GET_LIST_OF_ORDERS_WITHOUT_AUTH_ERROR_MESSAGE;
    }

}
