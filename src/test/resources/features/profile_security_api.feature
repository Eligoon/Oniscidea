
Feature: Profile API security

  Background:
    Given I have a profile for security testing

  Scenario: Reject a request without a token
    When I request that profile without a token
    Then I should receive a 401 status code for security

  Scenario: Reject a request with an invalid token
    When I request that profile with an invalid token
    Then I should receive a 401 status code for security

  Scenario: Allow a request with a valid token
    When I log in and request that profile with the token
    Then I should receive a 200 status code for security
