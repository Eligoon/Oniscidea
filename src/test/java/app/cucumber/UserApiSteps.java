package app.cucumber;

import app.Main;
import app.config.HibernateConfig;
import app.daos.GoogleCalendarConnectionDAO;
import app.services.GoogleCalendarService;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.javalin.Javalin;
import io.restassured.response.Response;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.ZonedDateTime;

import static io.restassured.RestAssured.given;

public class UserApiSteps {

    private static PostgreSQLContainer<?> postgres;
    private static EntityManagerFactory emf;
    private static Javalin app;

    private Response response;
    private Integer createdUserId;
    private String createdUserEmail;

    @BeforeAll
    public static void setup() {

        postgres =
                new PostgreSQLContainer<>("postgres:16-alpine")
                        .withDatabaseName("testdb")
                        .withUsername("test")
                        .withPassword("test");

        postgres.start();

        emf =
                HibernateConfig.createTestEntityManagerFactory(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword()
                );

        GoogleCalendarService googleCalendarService =
                new GoogleCalendarService(
                        new GoogleCalendarConnectionDAO(emf)
                ) {
                    @Override
                    public String createCalendar(Integer userId, String calendarName) {
                        return "test-calendar-" + userId;
                    }

                    @Override
                    public String createEvent(
                            Integer userId,
                            String googleCalendarId,
                            String title,
                            String description,
                            ZonedDateTime start,
                            ZonedDateTime end
                    ) {
                        return "test-event-" + userId;
                    }
                };

        app = Main.createApp(emf, googleCalendarService);

        app.start(7071);
    }

    @AfterAll
    public static void teardown() {

        if (app != null) {
            app.stop();
        }

        if (emf != null) {
            emf.close();
        }

        if (postgres != null) {
            postgres.stop();
        }
    }

    @Given("the API is running")
    public void theApiIsRunning() {

        Assertions.assertNotNull(app);
    }

    @When("I create a user with name {string} and email {string}")
    public void iCreateAUserWithNameAndEmail(
            String name,
            String email
    ) {

        createdUserEmail = email;

        String requestBody =
                """
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(name, email);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/users");

        Assertions.assertEquals(201, response.statusCode());

        createdUserId =
                response.jsonPath().getInt("id");
    }

    @When("I get the created user")
    public void iGetTheCreatedUser() {

        String token = loginAndGetToken(createdUserEmail);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/api/users/" + createdUserId);
    }

    @When("I update the created user to name {string} and email {string}")
    public void iUpdateTheCreatedUser(
            String name,
            String email
    ) {

        String token = loginAndGetToken(createdUserEmail);

        String requestBody =
                """
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(name, email);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .put("/api/users/" + createdUserId);
    }

    @When("I delete the created user")
    public void iDeleteTheCreatedUser() {

        String token = loginAndGetToken(createdUserEmail);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .delete("/api/users/" + createdUserId);
    }

    @When("I request all users without a token")
    public void iRequestAllUsersWithoutAToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .when()
                        .get("/api/users");
    }

    @When("I request all users with an invalid token")
    public void iRequestAllUsersWithAnInvalidToken() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer invalid-token")
                        .when()
                        .get("/api/users");
    }

    @When("I log in and request all users")
    public void iLogInAndRequestAllUsers() {

        String token = loginAndGetToken(createdUserEmail);

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/api/users");
    }

    private String loginAndGetToken(String email) {

        String requestBody =
                """
                {
                    "email": "%s",
                    "password": "password123"
                }
                """.formatted(email);

        Response loginResponse =
                given()
                        .baseUri("http://localhost:7071")
                        .contentType("application/json")
                        .body(requestBody)
                        .when()
                        .post("/api/login");

        Assertions.assertEquals(
                200,
                loginResponse.statusCode(),
                "Login failed: " + loginResponse.asString()
        );

        String token =
                loginResponse.jsonPath().getString("token");

        Assertions.assertNotNull(token, "Login response did not contain a token");

        return token;
    }

    @Then("I should receive a {int} status code")
    public void iShouldReceiveAStatusCode(
            int statusCode
    ) {

        Assertions.assertEquals(
                statusCode,
                response.statusCode()
        );
    }

    @And("the response should contain the name {string}")
    public void theResponseShouldContainTheName(
            String name
    ) {

        Assertions.assertEquals(
                name,
                response.jsonPath().getString("name")
        );
    }

    @And("the response should contain the email {string}")
    public void theResponseShouldContainTheEmail(
            String email
    ) {

        Assertions.assertEquals(
                email,
                response.jsonPath().getString("email")
        );
    }
}
