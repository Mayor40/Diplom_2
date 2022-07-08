import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserOrdersTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private User user;
    private int code;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
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
    @DisplayName("Check user orders")
    public void getOrdersWithAuth() {
        UserCredentials creds = UserCredentials.from(user);
        userClient.login(accessToken, creds);
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa75");
        ingredients.add("61c0c5a71d1f82001bdaaa6e");
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        Order order = new Order(ingredients);

        orderClient.createOrder(accessToken, order);

        Response response = orderClient.getOrders(accessToken);
        code = response.then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("orders.id", notNullValue())
                .extract()
                .statusCode();
    }

    @Test
    @DisplayName("Check user orders without authorization")
    public void getOrdersNoAuth() {
        Response response = orderClient.getOrdersNoAuth();
        code = response.then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"))
                .extract()
                .statusCode();
    }
}
