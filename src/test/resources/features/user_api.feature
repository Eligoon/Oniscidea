Feature: User API

  Scenario: Create a user

    Given the API is running
    When I create a user with name "API User" and email "api@test.com"
    Then I should receive a 201 status code
    And the response should contain the name "API User"
    And the response should contain the email "api@test.com"


  Scenario: Get a user by id

    Given the API is running
    When I create a user with name "Get User" and email "get@test.com"
    And I get the created user
    Then I should receive a 200 status code
    And the response should contain the name "Get User"
    And the response should contain the email "get@test.com"

  Scenario: Update a user

    Given the API is running
    When I create a user with name "Update User" and email "update@test.com"
    And I update the created user to name "Updated User" and email "updated@test.com"
    Then I should receive a 200 status code
    And the response should contain the name "Updated User"
    And the response should contain the email "updated@test.com"

  Scenario: Delete a user

    Given the API is running
    When I create a user with name "Delete User" and email "delete@test.com"
    And I delete the created user
    Then I should receive a 204 status code