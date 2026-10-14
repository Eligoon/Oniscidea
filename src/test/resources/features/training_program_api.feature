
Feature: Training Program API

  Scenario: Create a training program
    Given the API is running
    And I create a user for the training program
    When I create a training program with name "Push Day"
    Then I should receive a 201 status code for the training program
    And the training program response should contain the name "Push Day"

  Scenario: Prevent a user from accessing another user's training program
    Given the API is running
    And I create a user for the training program
    When I create a training program with name "Owner Program"
    And another user has a profile for the training program
    And I request that training program with the other user's token
    Then I should receive a 403 status code for the training program

  Scenario: Prevent a user from updating another user's training program
    Given the API is running
    And I create a user for the training program
    When I create a training program with name "Owner Program"
    And another user has a profile for the training program
    And I update that training program with the other user's token
    Then I should receive a 403 status code for the training program

  Scenario: Prevent a user from deleting another user's training program
    Given the API is running
    And I create a user for the training program
    When I create a training program with name "Owner Program"
    And another user has a profile for the training program
    And I delete that training program with the other user's token
    Then I should receive a 403 status code for the training program

  Scenario: Prevent a user from creating a training program for another user's profile
    Given the API is running
    And I create a user for the training program
    And another user has a profile for the training program
    When I create a training program using the other user's profile
    Then I should receive a 403 status code for the training program
