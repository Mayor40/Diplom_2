
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;


public class RegUserTest {
    private UserClient userClient;
    private String accessToken;
    private int code;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null | code == 200) {
            userClient.delete(accessToken).then().log().all();
        }
    }

    @Test
    @DisplayName("Check user registration")
    public void userCreateTest() {
        User user = User.getRandom();
        Response responseCreate = userClient.create(user);
        accessToken = responseCreate.then()
                .extract()
                .path("accessToken");

        UserCredentials creds = UserCredentials.from(user);
        userClient.login(accessToken, creds);

        code = responseCreate.then()
                .assertThat()
                .statusCode(200)
                .body("user.email", equalTo(user.getEmail().toLowerCase(Locale.ROOT)), "user.name", equalTo(user.getName()))
                .extract()
                .statusCode();
    }

    @Test
    @DisplayName("Check user registration of two users with the same credentials")
    public void userDuplicateTest() {
        User user = User.getRandom();
        Response responseCreate = userClient.create(user);
        accessToken = responseCreate.then()
                .extract()
                .path("accessToken");

        UserCredentials creds = UserCredentials.from(user);
        userClient.login(accessToken, creds);

        Response duplicate = userClient.create(user);

        code = duplicate.then()
                .assertThat()
                .statusCode(403)
                .body("message", equalTo("User already exists"))
                .extract()
                .statusCode();
    }

    @Test
    @DisplayName("Check user registration without password")
    public void userWithoutPasswordTest() {
        User user = User.getUserWithoutPassword();
        int expectedCode = 403;
        code = userClient.createUserWithoutPassword(user);

        Assert.assertEquals("Incorrect message", expectedCode, code);
    }
}
