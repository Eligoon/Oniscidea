package app.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import static io.restassured.RestAssured.given;

public class ProfileApiSteps {

    private Integer profileUserId;
    private Response response;

    @And("I create a user for the profile")
    public void iCreateAUserForTheProfile() {

        String requestBody =
                """
                {
                    "name": "Profile User",
                    "email": "profile@test.com",
                    "password": "password123"
                }
                """;

        profileUserId =
                given()
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

    @When("I create a profile with name {string}")
    public void iCreateAProfileWithName(
            String name
    ) {

        String requestBody =
                """
                {
                    "name": "%s",
                    "userId": %d
                }
                """.formatted(
                        name,
                        profileUserId
                );

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/profiles");
    }

    @Then("I should receive a {int} status code for the profile")
    public void iShouldReceiveAStatusCodeForTheProfile(
            int statusCode
    ) {

        Assertions.assertEquals(
                statusCode,
                response.statusCode()
        );
    }

    @And("the profile response should contain the name {string}")
    public void theProfileResponseShouldContainTheName(
            String name
    ) {

        Assertions.assertEquals(
                name,
                response.jsonPath().getString("name")
        );
    }
}