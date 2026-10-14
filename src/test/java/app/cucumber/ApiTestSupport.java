
package app.cucumber;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiTestSupport {

    private static final String BASE_URI = "http://localhost:7071";

    public static String loginAndGetToken(String email) {
        String requestBody = """
                {
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(email);

        Response response = given()
                .baseUri(BASE_URI)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/login");

        if (response.statusCode() != 200) {
            throw new AssertionError(
                    "Login failed with status " + response.statusCode()
                            + ": " + response.asString()
            );
        }

        return response.jsonPath().getString("token");
    }

    public static RequestSpecification authenticatedRequest(String token) {
        return given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer " + token);
    }

    public static RequestSpecification request() {
        return given().baseUri(BASE_URI);
    }
}
