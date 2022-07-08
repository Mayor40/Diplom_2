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

    private  String ROOT = "/auth";
    private  String REG = ROOT + "/register";
    private  String LOGIN = ROOT + "/login";
    private  String USER = ROOT + "/user";

    public Response create(User user) {
        return   RestAssured.given()
                .spec(requestSpec)
                .body(user)
                .log().all()
                .when()
                .post(REG);
    }

    public Response login(String accessToken, UserCredentials creds) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("authorization", accessToken)
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

    public Response changeData(String accessToken, User changedUser) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("authorization", accessToken)
                .body(changedUser)
                .when()
                .patch(USER);
    }
    public Response changeDataNoAuth(User changedUser) {
        return RestAssured.given()
                .spec(requestSpec)
                .body(changedUser)
                .when()
                .patch(USER);
    }

    public int createUserWithoutPassword(User user) {
        return RestAssured.given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post(REG)
                .then().log().all()
                .extract()
                .statusCode();
    }

    public Response delete(String accessToken) {
        return  RestAssured.given()
                .spec(requestSpec)
                .header("authorization", accessToken)
                .when()
                .delete(USER);
    }
}
