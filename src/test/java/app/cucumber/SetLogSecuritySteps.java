
package app.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class SetLogSecuritySteps {

    private String token;
    private String otherToken;
    private Integer exerciseLogId;
    private Integer setLogId;
    private Response response;

    @Given("I have an exercise log for another user")
    public void iHaveAnExerciseLogForAnotherUser() {
        createCurrentUser();

        String email = "setlog-owner-" + UUID.randomUUID() + "@test.com";
        Integer userId = createUser(email);
        otherToken = loginAndGetToken(email);

        Integer profileId = createProfile(
                userId,
                "Set Log Owner Profile",
                otherToken
        );

        exerciseLogId = createExerciseLogForUser(
                profileId,
                otherToken
        );
    }

    @Given("another user has a set log")
    public void anotherUserHasASetLog() {
        String email = "setlog-owner-" + UUID.randomUUID() + "@test.com";
        Integer userId = createUser(email);
        otherToken = loginAndGetToken(email);

        Integer profileId = createProfile(
                userId,
                "Set Log Owner Profile",
                otherToken
        );

        exerciseLogId = createExerciseLogForUser(
                profileId,
                otherToken
        );

        setLogId = createSetLog(
                exerciseLogId,
                otherToken
        );

        createCurrentUser();
    }

    @Given("I have an exercise log")
    public void iHaveAnExerciseLog() {
        String email = "setlog-user-" + UUID.randomUUID() + "@test.com";
        Integer userId = createUser(email);
        token = loginAndGetToken(email);

        Integer profileId = createProfile(
                userId,
                "Set Log Profile",
                token
        );

        exerciseLogId = createExerciseLogForUser(
                profileId,
                token
        );
    }

    @Given("I have a set log")
    public void iHaveASetLog() {
        String email = "setlog-user-" + UUID.randomUUID() + "@test.com";
        Integer userId = createUser(email);
        token = loginAndGetToken(email);

        Integer profileId = createProfile(
                userId,
                "Set Log Profile",
                token
        );

        exerciseLogId = createExerciseLogForUser(
                profileId,
                token
        );

        setLogId = createSetLog(
                exerciseLogId,
                token
        );
    }

    @When("I create a set log for that exercise log with my token")
    public void iCreateASetLogForThatExerciseLogWithMyToken() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                        {
                            "setNumber": 1,
                            "weight": 50.0,
                            "reps": 10,
                            "rir": 2,
                            "completed": true
                        }
                        """)
                .when()
                .post("/api/exercise-logs/" + exerciseLogId + "/sets");
    }

    @When("I retrieve that set log with my token")
    public void iRetrieveThatSetLogWithMyToken() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/set-logs/" + setLogId);
    }

    @Then("I should receive a {int} status code for set log security")
    public void iShouldReceiveAStatusCodeForSetLogSecurity(
            int statusCode
    ) {
        Assertions.assertEquals(
                statusCode,
                response.statusCode(),
                "Unexpected response body: " + response.asString()
        );
    }

    private void createCurrentUser() {
        String email = "setlog-current-" + UUID.randomUUID() + "@test.com";
        createUser(email);
        token = loginAndGetToken(email);
    }

    private Integer createUser(String email) {
        return given()
                .baseUri("http://localhost:7071")
                .contentType("application/json")
                .body("""
                        {
                            "name": "Set Log Test User",
                            "email": "%s",
                            "password": "password123"
                        }
                        """.formatted(email))
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
                            "password": "password123"
                        }
                        """.formatted(email))
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

    private Integer createExerciseLogForUser(
            Integer profileId,
            String userToken
    ) {
        Integer calendarId = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Set Log Calendar",
                            "profileId": %d
                        }
                        """.formatted(profileId))
                .when()
                .post("/api/calendars")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");

        Integer programId = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Set Log Program",
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

        Integer sessionId = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body("""
                        {
                            "calendarId": %d,
                            "trainingProgramId": %d,
                            "date": "2026-10-18",
                            "startTime": "10:00",
                            "endTime": "11:00"
                        }
                        """.formatted(calendarId, programId))
                .when()
                .post("/api/training-sessions")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");

        String exerciseName = "Set Log Exercise " + UUID.randomUUID();

        Response exerciseResponse = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body("""
                        {
                            "name": "%s",
                            "description": "Exercise created for SetLog security testing"
                        }
                        """.formatted(exerciseName))
                .when()
                .post("/api/exercises");

        Assertions.assertEquals(
                201,
                exerciseResponse.statusCode(),
                "Could not create exercise. Response body: "
                        + exerciseResponse.asString()
        );

        Integer exerciseId = exerciseResponse
                .jsonPath()
                .getInt("id");

        return given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body("""
                        {
                            "exerciseId": %d
                        }
                        """.formatted(exerciseId))
                .when()
                .post("/api/training-sessions/" + sessionId + "/logs")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    private Integer createSetLog(
            Integer logId,
            String userToken
    ) {
        return given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body("""
                        {
                            "setNumber": 1,
                            "weight": 50.0,
                            "reps": 10,
                            "rir": 2,
                            "completed": true
                        }
                        """)
                .when()
                .post("/api/exercise-logs/" + logId + "/sets")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }
}
