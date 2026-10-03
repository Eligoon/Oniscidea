package app.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import static io.restassured.RestAssured.given;

public class TrainingProgramApiSteps {

    private Integer trainingProgramProfileId;
    private Response response;

    @And("I create a user for the training program")
    public void iCreateAUserForTheTrainingProgram() {

        String requestBody =
                """
                {
                    "name": "Training Program User",
                    "email": "trainingprogram@test.com",
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
                                    "name": "Training Program Profile",
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

        trainingProgramProfileId =
                response.jsonPath().getInt("id");
    }


    @When("I create a training program with name {string}")
    public void iCreateATrainingProgramWithName(
            String name
    ) {

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
                response.statusCode()
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
}