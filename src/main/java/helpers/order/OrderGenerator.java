package helpers.order;

import models.order.Order;
import java.util.ArrayList;
import java.util.Arrays;

public class OrderGenerator {

    public Order getOrderWithCorrectIngredients() {

        return Order.builder()
                .ingredients(new ArrayList<>(Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa7a", "61c0c5a71d1f82001bdaaa79")))
                .build();
    }

    public Order getOrderWithIncorrectIngredients() {
        return new Order(new ArrayList<>(Arrays.asList("!@#", "$%%^", "^%#$")));
    }

    public Order getOrderWithNoIngredients() {
        return new Order(new ArrayList<>());
    }


}
