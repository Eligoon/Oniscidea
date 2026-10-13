
Feature: Login API

  Scenario: Login successfully and receive a valid JWT

    Given the API is running
    And I register a user for login
    When I log in with that user's credentials
    Then I should receive a 200 status code for login
    And the login response should contain a JWT
    And the JWT should be valid for that user
