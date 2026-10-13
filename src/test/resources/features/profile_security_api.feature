
Feature: Profile API security

  Background:
    Given I have a profile for security testing

  Scenario: Reject a request without a token
    When I request that profile without a token
    Then I should receive a 401 status code for security

  Scenario: Reject a request with an invalid token
    When I request that profile with an invalid token
    Then I should receive a 401 status code for security

  Scenario: Allow a user to access their own profile
    When I log in and request that profile with the token
    Then I should receive a 200 status code for security

  Scenario: Prevent a user from accessing another user's profile
    Given another user has a profile for security testing
    When I log in and request that other profile with my token
    Then I should receive a 403 status code for security

  Scenario: Prevent a user from updating another user's profile
    Given another user has a profile for security testing
    When I update that other profile with my token
    Then I should receive a 403 status code for security

  Scenario: Prevent a user from deleting another user's profile
    Given another user has a profile for security testing
    When I delete that other profile with my token
    Then I should receive a 403 status code for security
