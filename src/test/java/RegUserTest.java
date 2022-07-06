
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
    String accessToken;
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
        if (accessToken != null)
            userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check user registration")
    public void userCreateTest() {
        User user = User.getRandom();
        Response created = userClient.create(user);

        created.then()
                .assertThat()
                .statusCode(200)
                .body("user.email", equalTo(user.getEmail().toLowerCase(Locale.ROOT)), "user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Check user registration of two users with the same credentials")
    public void userDuplicateTest() {

        Response duplicate = userClient.create(user);

        duplicate.then()
                .assertThat()
                .statusCode(403)
                .body("message", equalTo("User already exists"));

    }

    @Test
    @DisplayName("Check user registration without password")
    public void userWithoutPasswordTest() {
        User user = User.getUserWithoutPassword();
        String expectedMessage = "Email, password and name are required fields";
        String actualMessage = userClient.createUserWithoutPassword(user);

        Assert.assertEquals("Incorrect message", expectedMessage, actualMessage);
    }
}
