
package app.cucumber;

import app.services.JwtService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Claims;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import static io.restassured.RestAssured.given;

public class LoginApiSteps {

    private final String email = "jwt-login@test.com";
    private final String password = "password123";

    private Integer userId;
    private Response response;
    private String token;

    @And("I register a user for login")
    public void iRegisterAUserForLogin() {

        String requestBody =
                """
                {
                    "name": "JWT Test User",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        Response registrationResponse =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/users");

        Assertions.assertEquals(
                201,
                registrationResponse.statusCode()
        );

        userId =
                registrationResponse.jsonPath().getInt("id");
    }

    @When("I log in with that user's credentials")
    public void iLogInWithThatUsersCredentials() {

        String requestBody =
                """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/login");

        token = response.jsonPath().getString("token");
    }

    @Then("I should receive a {int} status code for login")
    public void iShouldReceiveAStatusCodeForLogin(int statusCode) {

        Assertions.assertEquals(
                statusCode,
                response.statusCode()
        );
    }

    @And("the login response should contain a JWT")
    public void theLoginResponseShouldContainAJWT() {

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isBlank());

        Assertions.assertEquals(
                3,
                token.split("\\.", -1).length
        );
    }

    @And("the JWT should be valid for that user")
    public void theJWTShouldBeValidForThatUser() {

        Claims claims =
                new JwtService().validateToken(token);

        Assertions.assertEquals(
                userId.toString(),
                claims.getSubject()
        );

        Assertions.assertEquals(
                email,
                claims.get("email", String.class)
        );
    }
}
