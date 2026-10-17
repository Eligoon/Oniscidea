
package app.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class TrainingCalendarApiSteps {

    private Integer trainingCalendarProfileId;
    private Integer otherProfileId;
    private Integer trainingCalendarId;
    private Response response;
    private String token;
    private String otherToken;

    @And("I create a user for the training calendar")
    public void iCreateAUserForTheTrainingCalendar() {

        String email =
                "trainingcalendar-" + UUID.randomUUID() + "@test.com";

        Integer userId = createUser(email);

        token = loginAndGetToken(email);

        trainingCalendarProfileId = createProfile(
                userId,
                "Training Calendar Profile",
                token
        );
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
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/calendars");

        if (response.statusCode() == 201) {
            trainingCalendarId =
                    response.jsonPath().getInt("id");
        }
    }

    @And("another user has a profile for the training calendar")
    public void anotherUserHasAProfileForTheTrainingCalendar() {

        String email =
                "trainingcalendar-other-" + UUID.randomUUID() + "@test.com";

        Integer userId = createUser(email);

        otherToken = loginAndGetToken(email);

        otherProfileId = createProfile(
                userId,
                "Other Training Calendar Profile",
                otherToken
        );
    }

    @And("I request that training calendar with the other user's token")
    public void iRequestThatTrainingCalendarWithTheOtherUsersToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + otherToken)
                        .when()
                        .get("/api/calendars/" + trainingCalendarId);
    }

    @And("I update that training calendar with the other user's token")
    public void iUpdateThatTrainingCalendarWithTheOtherUsersToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType("application/json")
                        .body(
                                """
                                {
                                    "name": "Unauthorized Calendar Update"
                                }
                                """
                        )
                        .when()
                        .put("/api/calendars/" + trainingCalendarId);
    }

    @And("I delete that training calendar with the other user's token")
    public void iDeleteThatTrainingCalendarWithTheOtherUsersToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + otherToken)
                        .when()
                        .delete("/api/calendars/" + trainingCalendarId);
    }

    @When("I create a training calendar using the other user's profile")
    public void iCreateATrainingCalendarUsingTheOtherUsersProfile() {

        String requestBody =
                """
                {
                    "name": "Unauthorized Calendar",
                    "profileId": %d
                }
                """.formatted(otherProfileId);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + token)
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
                response.statusCode(),
                "Unexpected response body: " + response.asString()
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

    private Integer createUser(String email) {

        String requestBody =
                """
                {
                    "name": "Training Calendar User",
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(email);

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

    private String loginAndGetToken(String email) {

        return given()
                .baseUri("http://localhost:7071")
                .contentType("application/json")
                .body(
                        """
                        {
                            "email": "%s",
                            "password": "password123"
                        }
                        """.formatted(email)
                )
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
}
