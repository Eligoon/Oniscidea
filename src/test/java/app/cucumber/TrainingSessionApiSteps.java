package app.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static io.restassured.RestAssured.given;

public class TrainingSessionApiSteps {

    private Integer sessionProfileId;
    private Integer sessionCalendarId;
    private Integer sessionTrainingProgramId;
    private Response response;

    @And("I create a user for the training session")
    public void iCreateAUserForTheTrainingSession() {

        String requestBody =
                """
                {
                    "name": "Training Session User",
                    "email": "trainingsession@test.com",
                    "password": "password123"
                }
                """;

        Integer userId =
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

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(
                                """
                                {
                                    "name": "Training Session Profile",
                                    "userId": %d
                                }
                                """.formatted(userId)
                        )
                        .when()
                        .post("/api/profiles")
                        .then()
                        .statusCode(201)
                        .extract()
                        .response();

        sessionProfileId =
                response.jsonPath().getInt("id");

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(
                                """
                                {
                                    "name": "Training Session Calendar",
                                    "profileId": %d
                                }
                                """.formatted(sessionProfileId)
                        )
                        .when()
                        .post("/api/calendars")
                        .then()
                        .statusCode(201)
                        .extract()
                        .response();

        sessionCalendarId =
                response.jsonPath().getInt("id");
    }

    @And("I create a training program for the training session")
    public void iCreateATrainingProgramForTheTrainingSession() {

        String requestBody =
                """
                {
                    "name": "Training Session Program",
                    "description": "Training session test program",
                    "profileId": %d
                }
                """.formatted(
                        sessionProfileId
                );

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/training-programs")
                        .then()
                        .statusCode(201)
                        .extract()
                        .response();

        sessionTrainingProgramId =
                response.jsonPath().getInt("id");
    }

    @When("I create a training session for {string}")
    public void iCreateATrainingSessionFor(String date) {

        String requestBody =
                """
                {
                    "calendarId": %d,
                    "trainingProgramId": %d,
                    "date": "%s",
                    "startTime": "18:00:00",
                    "endTime": "19:00:00",
                    "completed": false,
                    "notes": "Training session test"
                }
                """.formatted(
                        sessionCalendarId,
                        sessionTrainingProgramId,
                        date
                );

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/training-sessions");
    }

    @Then("I should receive a {int} status code for the training session")
    public void iShouldReceiveAStatusCodeForTheTrainingSession(int statusCode) {

        Assertions.assertEquals(
                statusCode,
                response.statusCode()
        );
    }

    @And("the training session response should contain the date {string}")
    public void theTrainingSessionResponseShouldContainTheDate(String date) {

        List<Integer> responseDate =
                response.jsonPath().getList("date");

        String actualDate =
                String.format(
                        "%04d-%02d-%02d",
                        responseDate.get(0),
                        responseDate.get(1),
                        responseDate.get(2)
                );

        Assertions.assertEquals(
                date,
                actualDate
        );
    }
}