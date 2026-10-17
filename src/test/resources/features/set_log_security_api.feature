
Feature: Set log ownership security

  Scenario: User cannot create a set log for another user's exercise log
    Given I have an exercise log for another user
    When I create a set log for that exercise log with my token
    Then I should receive a 403 status code for set log security

  Scenario: User cannot retrieve another user's set log
    Given another user has a set log
    When I retrieve that set log with my token
    Then I should receive a 403 status code for set log security

  Scenario: User can create their own set log
    Given I have an exercise log
    When I create a set log for that exercise log with my token
    Then I should receive a 201 status code for set log security

  Scenario: User can retrieve their own set log
    Given I have a set log
    When I retrieve that set log with my token
    Then I should receive a 200 status code for set log security
