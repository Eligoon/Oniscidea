Feature: Training Calendar API

  Scenario: Create a training calendar

    Given the API is running
    And I create a user for the training calendar
    When I create a training calendar with name "My Training Calendar"
    Then I should receive a 201 status code for the training calendar
    And the training calendar response should contain the name "My Training Calendar"