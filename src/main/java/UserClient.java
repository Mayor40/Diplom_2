import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;

import io.restassured.http.ContentType;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class UserClient extends RestAssuredClient {

    RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://stellarburgers.nomoreparties.site/api")
            .setContentType(ContentType.JSON)
            .build();

    private final String ROOT = "/auth";
    private final String REG = ROOT + "/register";
    private final String LOGIN = ROOT + "/login";
    private final String USER = ROOT + "/user";

    public Response create(User user) {
        return reqSpec
                .body(user)
                .log().all()
                .when()
                .post(REG);
    }

    public Response login(String accessToken, UserCredentials creds) {
        return reqSpec
                .header("authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(creds)
                .log().all()
                .when()
                .post(LOGIN);
    }

    public Response getData(String accessToken) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("authorization", accessToken)
                .log().all()
                .when()
                .get(USER);
    }

    public Response changeData(User changedUser) {
        return reqSpec
                .contentType(ContentType.JSON)
                .body(changedUser)
                .when()
                .patch(USER);
    }

    public String createUserWithoutPassword(User user) {
        return reqSpec
                .body(user)
                .when()
                .post(REG)
                .then().log().all()
                .assertThat()
                .statusCode(403)
                .extract()
                .path("message");
    }

    public void delete(String accessToken) {
        reqSpec
                .when()
                .delete(USER);
    }

}
