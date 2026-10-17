
package app.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class NoteSecuritySteps {

    private final String password = "password123";

    private String token;
    private String otherToken;
    private Integer profileId;
    private Integer otherProfileId;
    private Integer noteId;
    private Response response;

    @Given("I have a profile for note security")
    public void iHaveAProfileForNoteSecurity() {
        String email = "note-user-" + UUID.randomUUID() + "@test.com";
        Integer userId = createUser(email);
        token = loginAndGetToken(email);

        profileId = createProfile(
                userId,
                "Note Security Profile",
                token
        );
    }

    @Given("another user has a profile for note security")
    public void anotherUserHasAProfileForNoteSecurity() {
        String email = "note-other-" + UUID.randomUUID() + "@test.com";
        Integer userId = createUser(email);
        otherToken = loginAndGetToken(email);

        otherProfileId = createProfile(
                userId,
                "Other Note Security Profile",
                otherToken
        );
    }

    @Given("another user has a note for note security")
    public void anotherUserHasANoteForNoteSecurity() {
        String email = "note-owner-" + UUID.randomUUID() + "@test.com";
        Integer userId = createUser(email);
        otherToken = loginAndGetToken(email);

        otherProfileId = createProfile(
                userId,
                "Note Owner Profile",
                otherToken
        );

        noteId = createNote(
                otherProfileId,
                otherToken
        );

        createCurrentUser();
    }

    @Given("I have a note for note security")
    public void iHaveANoteForNoteSecurity() {
        String email = "note-owner-" + UUID.randomUUID() + "@test.com";
        Integer userId = createUser(email);
        token = loginAndGetToken(email);

        profileId = createProfile(
                userId,
                "Note Owner Profile",
                token
        );

        noteId = createNote(
                profileId,
                token
        );
    }

    @When("I create a note for the other user's profile")
    public void iCreateANoteForTheOtherUsersProfile() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                        {
                            "title": "Unauthorized Note",
                            "text": "This note should not be created",
                            "profileId": %d
                        }
                        """.formatted(otherProfileId))
                .when()
                .post("/api/notes");
    }

    @When("I retrieve that note with my token")
    public void iRetrieveThatNoteWithMyToken() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/notes/" + noteId);
    }

    @When("I update that note with my token")
    public void iUpdateThatNoteWithMyToken() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                        {
                            "title": "Unauthorized Update",
                            "text": "This note should not be changed"
                        }
                        """)
                .when()
                .put("/api/notes/" + noteId);
    }

    @When("I delete that note with my token")
    public void iDeleteThatNoteWithMyToken() {
        response = given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/notes/" + noteId);
    }

    @Then("I should receive a {int} status code for note security")
    public void iShouldReceiveAStatusCodeForNoteSecurity(int statusCode) {
        Assertions.assertEquals(
                statusCode,
                response.statusCode(),
                "Unexpected response body: " + response.asString()
        );
    }

    private void createCurrentUser() {
        String email = "note-current-" + UUID.randomUUID() + "@test.com";
        Integer userId = createUser(email);
        token = loginAndGetToken(email);

        profileId = createProfile(
                userId,
                "Current Note Profile",
                token
        );
    }

    private Integer createNote(
            Integer noteProfileId,
            String userToken
    ) {
        return given()
                .baseUri("http://localhost:7071")
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body("""
                        {
                            "title": "Security Test Note",
                            "text": "Note created for ownership testing",
                            "profileId": %d
                        }
                        """.formatted(noteProfileId))
                .when()
                .post("/api/notes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }

    private Integer createUser(String email) {
        return given()
                .baseUri("http://localhost:7071")
                .contentType("application/json")
                .body("""
                        {
                            "name": "Note Security Test User",
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
