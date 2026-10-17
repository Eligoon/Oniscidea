
package app.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class TrainingProgramExerciseSecuritySteps {

    private final String password = "password123";

    private Integer userId;
    private Integer profileId;
    private Integer trainingProgramId;
    private Integer exerciseId;
    private String ownerToken;
    private String token;
    private Response response;

    @Given("I have a training program and an exercise for exercise security")
    public void iHaveATrainingProgramAndAnExerciseForExerciseSecurity() {
        String email =
                "program-exercise-" + UUID.randomUUID() + "@test.com";

        userId = createUser(email);
        ownerToken = loginAndGetToken(email);
        token = ownerToken;

        profileId = createProfile(
                userId,
                "Program Exercise Security Profile",
                ownerToken
        );

        trainingProgramId = createTrainingProgram();

        exerciseId = createExercise();
    }

    @Given("another user is authenticated for exercise security")
    public void anotherUserIsAuthenticatedForExerciseSecurity() {
        String email =
                "program-exercise-other-" + UUID.randomUUID() + "@test.com";

        createUser(email);
        token = loginAndGetToken(email);
    }

    @Given("the exercise is added to the training program")
    public void theExerciseIsAddedToTheTrainingProgram() {
        response = addExercise();

        Assertions.assertEquals(
                201,
                response.statusCode(),
                "Could not add exercise. Response body: "
                        + response.asString()
        );
    }

    @When("I add the exercise to the training program")
    public void iAddTheExerciseToTheTrainingProgram() {
        response = addExercise();
    }

    @When("I delete the exercise from the training program")
    public void iDeleteTheExerciseFromTheTrainingProgram() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(
                        "/api/training-programs/"
                                + trainingProgramId
                                + "/exercises/"
                                + exerciseId
                );
    }

    @Then("I should receive a {int} status code for training program exercise security")
    public void iShouldReceiveAStatusCodeForTrainingProgramExerciseSecurity(
            int statusCode
    ) {
        Assertions.assertEquals(
                statusCode,
                response.statusCode(),
                "Unexpected response body: " + response.asString()
        );
    }

    private Response addExercise() {
        return given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                        {
                            "exerciseId": %d,
                            "exerciseOrder": 1,
                            "plannedSets": 3,
                            "plannedReps": 10,
                            "restSeconds": 60
                        }
                        """.formatted(exerciseId))
                .when()
                .post(
                        "/api/training-programs/"
                                + trainingProgramId
                                + "/exercises"
                );
    }

    private Integer createTrainingProgram() {
        return given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Security Test Program",
                            "description": "Program exercise security test",
                            "profileId": %d
                        }
                        """.formatted(profileId))
                .when()
                .post("/api/training-programs")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    private Integer createExercise() {
        Response exerciseResponse = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Security Exercise %s",
                            "description": "Exercise for security testing"
                        }
                        """.formatted(UUID.randomUUID()))
                .when()
                .post("/api/exercises");

        Assertions.assertEquals(
                201,
                exerciseResponse.statusCode(),
                "Could not create exercise. Response body: "
                        + exerciseResponse.asString()
        );

        return exerciseResponse.jsonPath().getInt("id");
    }

    private Integer createUser(String email) {
        return given()
                .baseUri("http://localhost:7071")
                .contentType("application/json")
                .body("""
                        {
                            "name": "Program Exercise Test User",
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(email, password))
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    private String loginAndGetToken(String email) {
        return given()
                .baseUri("http://localhost:7071")
                .contentType("application/json")
                .body("""
                        {
                            "email": "%s",
                            "password": "%s"
                        }
                        """.formatted(email, password))
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }

    private Integer createProfile(
            Integer userId,
            String name,
            String userToken
    ) {
        return given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "%s",
                            "userId": %d
                        }
                        """.formatted(name, userId))
                .when()
                .post("/api/profiles")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }
}
