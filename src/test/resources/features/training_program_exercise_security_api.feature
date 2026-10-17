
Feature: Training Program Exercise Security API

  Scenario: Prevent a user from adding an exercise to another user's training program
    Given the API is running
    And I have a training program and an exercise for exercise security
    And another user is authenticated for exercise security
    When I add the exercise to the training program
    Then I should receive a 403 status code for training program exercise security

  Scenario: Prevent a user from deleting an exercise from another user's training program
    Given the API is running
    And I have a training program and an exercise for exercise security
    And the exercise is added to the training program
    And another user is authenticated for exercise security
    When I delete the exercise from the training program
    Then I should receive a 403 status code for training program exercise security

  Scenario: Allow a user to add an exercise to their own training program
    Given the API is running
    And I have a training program and an exercise for exercise security
    When I add the exercise to the training program
    Then I should receive a 201 status code for training program exercise security

  Scenario: Allow a user to delete an exercise from their own training program
    Given the API is running
    And I have a training program and an exercise for exercise security
    And the exercise is added to the training program
    When I delete the exercise from the training program
    Then I should receive a 204 status code for training program exercise security
