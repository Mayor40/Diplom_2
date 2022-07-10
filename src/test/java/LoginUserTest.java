import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


public class LoginUserTest {

    private UserClient userClient;
    private String accessToken;
    private User user;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();

        Response responseCreate = userClient.create(user);
        accessToken = responseCreate.then()
                .extract()
                .path("accessToken");
    }

    @After
    public void tearDown() {
            userClient.delete(accessToken).then().log().all();
    }

    @Test
    @DisplayName("Check positive authorization")
    public void successLoginTest() {
        UserCredentials creds = UserCredentials.from(user);

        Response responseLogin = userClient.login(accessToken, creds);
        responseLogin.then()
                .assertThat()
                .statusCode(200)
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Check negative authorization with incorrect email")
    public void incorrectCredsTest() {
        UserCredentials creds = UserCredentials.from(user);
        creds.setEmail("www" + user.getEmail());

        Response incorrectCreds = userClient.login(accessToken, creds);
        incorrectCreds.then()
                .assertThat()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}
