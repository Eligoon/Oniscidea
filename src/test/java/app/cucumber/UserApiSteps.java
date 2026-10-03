package app.cucumber;

import app.Main;
import app.config.HibernateConfig;
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

import static io.restassured.RestAssured.given;

public class UserApiSteps {

    private static PostgreSQLContainer<?> postgres;
    private static EntityManagerFactory emf;
    private static Javalin app;

    private Response response;
    private Integer createdUserId;

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

        app =
                Main.createApp(emf);

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

        createdUserId =
                response.jsonPath().getInt("id");
    }

    @When("I get the created user")
    public void iGetTheCreatedUser() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .when()
                        .get("/api/users/" + createdUserId);
    }

    @When("I update the created user to name {string} and email {string}")
    public void iUpdateTheCreatedUser(
            String name,
            String email
    ) {

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
                        .put("/api/users/" + createdUserId);
    }

    @When("I delete the created user")
    public void iDeleteTheCreatedUser() {

        response =
                given()
                        .baseUri("http://localhost:7071")
                        .when()
                        .delete("/api/users/" + createdUserId);
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