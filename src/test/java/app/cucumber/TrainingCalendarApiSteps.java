package app.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import static io.restassured.RestAssured.given;

public class TrainingCalendarApiSteps {

    private Integer trainingCalendarProfileId;
    private Response response;

    @And("I create a user for the training calendar")
    public void iCreateAUserForTheTrainingCalendar() {

        String requestBody =
                """
                {
                    "name": "Training Calendar User",
                    "email": "trainingcalendar@test.com",
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
                                    "name": "Training Calendar Profile",
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

        trainingCalendarProfileId =
                response.jsonPath().getInt("id");
    }

    @When("I create a training calendar with name {string}")
    public void iCreateATrainingCalendarWithName(
            String name
    ) {

        String requestBody =
                """
                {
                    "name": "%s",
                    "profileId": %d
                }
                """.formatted(
                        name,
                        trainingCalendarProfileId
                );

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/calendars");
    }

    @Then("I should receive a {int} status code for the training calendar")
    public void iShouldReceiveAStatusCodeForTheTrainingCalendar(
            int statusCode
    ) {

        Assertions.assertEquals(
                statusCode,
                response.statusCode()
        );
    }

    @And("the training calendar response should contain the name {string}")
    public void theTrainingCalendarResponseShouldContainTheName(
            String name
    ) {

        Assertions.assertEquals(
                name,
                response.jsonPath().getString("name")
        );
    }
}