
Feature: Training Calendar API

  Scenario: Create a training calendar
    Given the API is running
    And I create a user for the training calendar
    When I create a training calendar with name "My Training Calendar"
    Then I should receive a 201 status code for the training calendar
    And the training calendar response should contain the name "My Training Calendar"

  Scenario: Prevent a user from accessing another user's training calendar
    Given the API is running
    And I create a user for the training calendar
    When I create a training calendar with name "Owner Calendar"
    And another user has a profile for the training calendar
    And I request that training calendar with the other user's token
    Then I should receive a 403 status code for the training calendar

  Scenario: Prevent a user from updating another user's training calendar
    Given the API is running
    And I create a user for the training calendar
    When I create a training calendar with name "Owner Calendar"
    And another user has a profile for the training calendar
    And I update that training calendar with the other user's token
    Then I should receive a 403 status code for the training calendar

  Scenario: Prevent a user from deleting another user's training calendar
    Given the API is running
    And I create a user for the training calendar
    When I create a training calendar with name "Owner Calendar"
    And another user has a profile for the training calendar
    And I delete that training calendar with the other user's token
    Then I should receive a 403 status code for the training calendar

  Scenario: Prevent a user from creating a calendar for another user's profile
    Given the API is running
    And I create a user for the training calendar
    And another user has a profile for the training calendar
    When I create a training calendar using the other user's profile
    Then I should receive a 403 status code for the training calendar
