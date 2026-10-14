
package app.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class TrainingProgramApiSteps {

    private final String password = "password123";

    private Integer trainingProgramProfileId;
    private Integer otherProfileId;
    private Integer trainingProgramId;
    private String ownerEmail;
    private String ownerToken;
    private String otherToken;
    private Response response;

    @And("I create a user for the training program")
    public void iCreateAUserForTheTrainingProgram() {

        ownerEmail =
                "trainingprogram-" + UUID.randomUUID() + "@test.com";

        Integer userId = createUser(ownerEmail);

        ownerToken = loginAndGetToken(ownerEmail);

        trainingProgramProfileId = createProfile(
                userId,
                "Training Program Profile",
                ownerToken
        );
    }

    @When("I create a training program with name {string}")
    public void iCreateATrainingProgramWithName(String name) {

        String requestBody =
                """
                {
                    "name": "%s",
                    "description": "Training program description",
                    "profileId": %d
                }
                """.formatted(
                        name,
                        trainingProgramProfileId
                );

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/training-programs");

        if (response.statusCode() == 201) {
            trainingProgramId =
                    response.jsonPath().getInt("id");
        }
    }

    @Given("another user has a profile for the training program")
    public void anotherUserHasAProfileForTheTrainingProgram() {

        String otherEmail =
                "trainingprogram-other-" + UUID.randomUUID() + "@test.com";

        Integer otherUserId = createUser(otherEmail);

        otherToken = loginAndGetToken(otherEmail);

        otherProfileId = createProfile(
                otherUserId,
                "Other Training Program Profile",
                otherToken
        );
    }

    @And("I request that training program with the other user's token")
    public void iRequestThatTrainingProgramWithTheOtherUsersToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + otherToken)
                        .when()
                        .get("/api/training-programs/" + trainingProgramId);
    }

    @And("I update that training program with the other user's token")
    public void iUpdateThatTrainingProgramWithTheOtherUsersToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType("application/json")
                        .body(
                                """
                                {
                                    "name": "Unauthorized Update",
                                    "description": "Updated by another user"
                                }
                                """
                        )
                        .when()
                        .put("/api/training-programs/" + trainingProgramId);
    }

    @And("I delete that training program with the other user's token")
    public void iDeleteThatTrainingProgramWithTheOtherUsersToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + otherToken)
                        .when()
                        .delete("/api/training-programs/" + trainingProgramId);
    }

    @When("I create a training program using the other user's profile")
    public void iCreateATrainingProgramUsingTheOtherUsersProfile() {

        String requestBody =
                """
                {
                    "name": "Unauthorized Program",
                    "description": "Should not be created",
                    "profileId": %d
                }
                """.formatted(otherProfileId);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/training-programs");
    }

    @Then("I should receive a {int} status code for the training program")
    public void iShouldReceiveAStatusCodeForTheTrainingProgram(
            int statusCode
    ) {

        Assertions.assertEquals(
                statusCode,
                response.statusCode(),
                "Unexpected response body: " + response.asString()
        );
    }

    @And("the training program response should contain the name {string}")
    public void theTrainingProgramResponseShouldContainTheName(
            String name
    ) {

        Assertions.assertEquals(
                name,
                response.jsonPath().getString("name")
        );
    }

    private Integer createUser(String email) {

        String requestBody =
                """
                {
                    "name": "Training Program Test User",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

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
            String token
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
                .header("Authorization", "Bearer " + token)
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

    private String loginAndGetToken(String email) {

        String requestBody =
                """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

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
