Feature: Training Program API

  Scenario: Create a training program

    Given the API is running
    And I create a user for the training program
    When I create a training program with name "Push Day"
    Then I should receive a 201 status code for the training program
    And the training program response should contain the name "Push Day"