
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.hamcrest.Matchers.nullValue;

public class OrderTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private User user;
    private Ingredients ing;
    private int code;


    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        ing = new Ingredients();
        user = User.getRandom();

        Response responseCreate = userClient.create(user);
        accessToken = responseCreate.then()
                .extract()
                .path("accessToken");
    }

    @After
    public void tearDown() {
        if (code == 200) {
            userClient.delete(accessToken).then().log().all();
        } else {
            UserCredentials creds = UserCredentials.from(user);
            userClient.login(accessToken, creds);
            userClient.delete(accessToken).then().log().all();
        }
    }


    @Test
    @DisplayName("Check positive order creation")
    public void createOrder() {
        UserCredentials creds = UserCredentials.from(user);
        Response response = userClient.login(accessToken, creds);
        code = response.then()
                .extract()
                .statusCode();
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(ing.getIngredient1());
        ingredients.add(ing.getIngredient2());
        ingredients.add(ing.getIngredient3());
        Order order = new Order(ingredients);

        Response createdOrder = orderClient.createOrder(accessToken, order);

        createdOrder.then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("order.ingredients", notNullValue());

    }

    @Test
    @DisplayName("Check order creation without authorization")
    public void createOrderWithoutAuth() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(ing.getIngredient1());
        ingredients.add(ing.getIngredient2());
        ingredients.add(ing.getIngredient3());
        Order order = new Order(ingredients);

        Response createdOrder = orderClient.createOrderNoAuth(order);

        createdOrder.then()
                .log().all()
                .statusCode(200)
                .body("order.createdAt", nullValue())
                .body("order.owner", nullValue());
    }

    @Test
    @DisplayName("Check negative order creation without ingredients")
    public void createOrderWithoutIngredients() {
        UserCredentials creds = UserCredentials.from(user);
        Response response = userClient.login(accessToken, creds);
        code = response.then()
                .extract()
                .statusCode();
        Order order = new Order();

        Response createdOrder = orderClient.createOrder(accessToken, order);

        createdOrder.then()
                .log().all()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Check negative order creation with incorrect ingredient hashcode")
    public void createOrderWrongHash() {
        UserCredentials creds = UserCredentials.from(user);
        Response response = userClient.login(accessToken, creds);
        code = response.then()
                .extract()
                .statusCode();
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(ing.getIngredient1() + "p");
        ingredients.add(ing.getIngredient2());
        ingredients.add(ing.getIngredient3());
        Order order = new Order(ingredients);

        Response createdOrder = orderClient.createOrder(accessToken, order);

        createdOrder.then()
                .log().all()
                .assertThat()
                .statusCode(500);
    }
}
