import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;


public class ChangeUserDataTest {

    private UserClient userClient;
    private OrderClient orderClient;
    String accessToken;
    private User user;

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
        if (accessToken != null)
            userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check positive changing user data")
    public void changeUserDataPositiveTest() {
        UserCredentials creds = UserCredentials.from(user);

        userClient.login(accessToken, creds);
        userClient.getData(accessToken);

        User changeUser = User.getUserWithoutPassword();
        Response changedUser = userClient.changeData(changeUser);
        changedUser.then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("user.email", equalTo(changeUser.getEmail().toLowerCase(Locale.ROOT)), "user.name", equalTo(changeUser.getName()));
    }

    @Test
    @DisplayName("Check negative changing user data")
    public void changeUserDataNegativeTest() {
        userClient.getData(accessToken);

        User changeUser = User.getUserWithoutPassword();
        Response changedUser = userClient.changeData(changeUser);
        changedUser.then()
                .log().all()
                .assertThat()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
}
