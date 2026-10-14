package app.cucumber;

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

    private String otherEmail;
    private Integer profileId;
    private Integer otherProfileId;
    private Response response;
    private String token;

    @Given("I have a profile for security testing")
    public void iHaveAProfileForSecurityTesting() {

        Integer userId = createUser(email);
        token = loginAndGetToken(email);

        profileId = createProfile(
                userId,
                "Security Test Profile",
                token
        );
    }

    @Given("another user has a profile for security testing")
    public void anotherUserHasAProfileForSecurityTesting() {

        otherEmail =
                "security-other-" + UUID.randomUUID() + "@test.com";

        Integer otherUserId = createUser(otherEmail);
        String otherToken = loginAndGetToken(otherEmail);

        otherProfileId = createProfile(
                otherUserId,
                "Other Security Test Profile",
                otherToken
        );
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

        token = loginAndGetToken(email);

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

    @When("I log in and request that other profile with my token")
    public void iLogInAndRequestThatOtherProfileWithMyToken() {

        token = loginAndGetToken(email);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .when()
                        .get("/api/profiles/" + otherProfileId);
    }

    @When("I update that other profile with my token")
    public void iUpdateThatOtherProfileWithMyToken() {

        token = loginAndGetToken(email);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType("application/json")
                        .body(
                                """
                                {
                                    "name": "Unauthorized Update"
                                }
                                """
                        )
                        .when()
                        .put("/api/profiles/" + otherProfileId);
    }

    @When("I delete that other profile with my token")
    public void iDeleteThatOtherProfileWithMyToken() {

        token = loginAndGetToken(email);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .when()
                        .delete("/api/profiles/" + otherProfileId);
    }

    @Then("I should receive a {int} status code for security")
    public void iShouldReceiveAStatusCodeForSecurity(int statusCode) {

        Assertions.assertEquals(
                statusCode,
                response.statusCode()
        );
    }

    private Integer createUser(String userEmail) {

        String requestBody =
                """
                {
                    "name": "Security Test User",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(userEmail, password);

        return given()
                .baseUri("http://localhost:7071")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    private Integer createProfile(
            Integer userId,
            String profileName,
            String profileToken
    ) {

        String requestBody =
                """
                {
                    "name": "%s",
                    "userId": %d
                }
                """.formatted(profileName, userId);

        return given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + profileToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/profiles")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    private String loginAndGetToken(String userEmail) {

        String requestBody =
                """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(userEmail, password);

        return given()
                .baseUri("http://localhost:7071")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }
}
