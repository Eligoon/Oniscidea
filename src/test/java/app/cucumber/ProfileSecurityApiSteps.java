
package app.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class ProfileSecurityApiSteps {

    private final String email =
            "security-" + UUID.randomUUID() + "@test.com";

    private final String password = "password123";

    private Integer profileId;
    private Response response;

    @Given("I have a profile for security testing")
    public void iHaveAProfileForSecurityTesting() {

        String userRequest =
                """
                {
                    "name": "Security Test User",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        Integer userId =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(userRequest)
                        .when()
                        .post("/api/users")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getInt("id");

        String profileRequest =
                """
                {
                    "name": "Security Test Profile",
                    "userId": %d
                }
                """.formatted(userId);

        profileId =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(profileRequest)
                        .when()
                        .post("/api/profiles")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getInt("id");
    }

    @When("I request that profile without a token")
    public void iRequestThatProfileWithoutAToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .when()
                        .get("/api/profiles/" + profileId);
    }

    @When("I request that profile with an invalid token")
    public void iRequestThatProfileWithAnInvalidToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header(
                                "Authorization",
                                "Bearer invalid.token.value"
                        )
                        .when()
                        .get("/api/profiles/" + profileId);
    }

    @When("I log in and request that profile with the token")
    public void iLogInAndRequestThatProfileWithTheToken() {

        String loginRequest =
                """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        String token =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(loginRequest)
                        .when()
                        .post("/api/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getString("token");

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isBlank());

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .when()
                        .get("/api/profiles/" + profileId);
    }

    @Then("I should receive a {int} status code for security")
    public void iShouldReceiveAStatusCodeForSecurity(int statusCode) {

        Assertions.assertEquals(
                statusCode,
                response.statusCode()
        );
    }
}
