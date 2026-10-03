Feature: Training Session API

  Scenario: Create a training session

    Given the API is running
    And I create a user for the training session
    And I create a training program for the training session
    When I create a training session for "2026-10-05"
    Then I should receive a 201 status code for the training session
    And the training session response should contain the date "2026-10-05"