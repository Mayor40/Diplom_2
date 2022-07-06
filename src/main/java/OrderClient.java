import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


public class OrderClient extends RestAssuredClient {

    private final String ORDER = URL + "/orders";

    RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://stellarburgers.nomoreparties.site/api")
            .setContentType(ContentType.JSON)
            .build();

    public Response createOrder(String accessToken, Order order) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("authorization", accessToken)
                .body(order)
                .log().all()
                .when()
                .post(ORDER);
    }

    public Response createOrderNoAuth(Order order) {
        return RestAssured.given()
                .spec(requestSpec)
                .body(order)
                .log().all()
                .when()
                .post(ORDER);
    }

    public Response getOrders(String accessToken) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("authorization", accessToken)
                .log().all()
                .when()
                .get(ORDER);
    }

    public Response getOrdersNoAuth() {
        return RestAssured.given()
                .spec(requestSpec)
                .log().all()
                .when()
                .get(ORDER);
    }
}
