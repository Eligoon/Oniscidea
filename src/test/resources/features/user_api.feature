Feature: User API

  Scenario: Create a user

    Given the API is running
    When I create a user with name "API User" and email "create-user@test.com"
    Then I should receive a 201 status code
    And the response should contain the name "API User"
    And the response should contain the email "create-user@test.com"

  Scenario: Get a user by id

    Given the API is running
    When I create a user with name "Get User" and email "get-user@test.com"
    And I get the created user
    Then I should receive a 200 status code
    And the response should contain the name "Get User"
    And the response should contain the email "get-user@test.com"

  Scenario: Update a user

    Given the API is running
    When I create a user with name "Update User" and email "update-user@test.com"
    And I update the created user to name "Updated User" and email "updated-user@test.com"
    Then I should receive a 200 status code
    And the response should contain the name "Updated User"
    And the response should contain the email "updated-user@test.com"

  Scenario: Delete a user

    Given the API is running
    When I create a user with name "Delete User" and email "delete-user@test.com"
    And I delete the created user
    Then I should receive a 204 status code

  Scenario: Get all users without a token

    Given the API is running
    When I request all users without a token
    Then I should receive a 401 status code

  Scenario: Get all users with an invalid token

    Given the API is running
    When I request all users with an invalid token
    Then I should receive a 401 status code

  Scenario: Get all users with a valid token

    Given the API is running
    When I create a user with name "Secure User" and email "secure-user@test.com"
    And I log in and request all users
    Then I should receive a 200 status code