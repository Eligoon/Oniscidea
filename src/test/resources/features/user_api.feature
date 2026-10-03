Feature: User API

  Scenario: Create a user

    Given the API is running
    When I create a user with name "API User" and email "api@test.com"
    Then I should receive a 201 status code
    And the response should contain the name "API User"
    And the response should contain the email "api@test.com"