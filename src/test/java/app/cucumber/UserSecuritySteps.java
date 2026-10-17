package app.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class UserSecuritySteps {

    private final String password = "password123";

    private String token;
    private Integer userId;
    private Integer otherUserId;
    private Response response;

    @Given("two users exist for user security tests")
    public void twoUsersExistForUserSecurityTests() {
        String email = "user-security-" + UUID.randomUUID() + "@test.com";
        userId = createUser(email);
        token = loginAndGetToken(email);

        String otherEmail = "other-user-security-" + UUID.randomUUID() + "@test.com";
        otherUserId = createUser(otherEmail);
    }

    @When("the first user retrieves the second user's account")
    public void firstUserRetrievesSecondUsersAccount() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/users/" + otherUserId);
    }

    @When("the first user updates the second user's account")
    public void firstUserUpdatesSecondUsersAccount() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                        {
                            "name": "Unauthorized Update",
                            "email": "updated-%s@test.com",
                            "password": "%s"
                        }
                        """.formatted(UUID.randomUUID(), password))
                .when()
                .put("/api/users/" + otherUserId);
    }

    @When("the first user deletes the second user's account")
    public void firstUserDeletesSecondUsersAccount() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/users/" + otherUserId);
    }

    @When("the first user retrieves the users list")
    public void firstUserRetrievesUsersList() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/users");
    }

    @Then("the user security response status should be {int}")
    public void userSecurityResponseStatusShouldBe(int statusCode) {
        Assertions.assertEquals(
                statusCode,
                response.statusCode(),
                "Unexpected response body: " + response.asString()
        );
    }

    @And("the users list should contain only the first user")
    public void usersListShouldContainOnlyFirstUser() {
        Assertions.assertEquals(
                1,
                response.jsonPath().getList("$").size(),
                "Unexpected response body: " + response.asString()
        );

        Assertions.assertEquals(
                userId,
                response.jsonPath().getInt("[0].id")
        );
    }

    private Integer createUser(String email) {
        return given()
                .baseUri("http://localhost:7071")
                .contentType("application/json")
                .body("""
                        {
                            "name": "User Security Test User",
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
}
