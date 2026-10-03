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