
Feature: Note Security API

  Scenario: Prevent creating a note for another user's profile
    Given the API is running
    And I have a profile for note security
    And another user has a profile for note security
    When I create a note for the other user's profile
    Then I should receive a 403 status code for note security

  Scenario: Prevent retrieving another user's note
    Given the API is running
    And another user has a note for note security
    When I retrieve that note with my token
    Then I should receive a 403 status code for note security

  Scenario: Prevent updating another user's note
    Given the API is running
    And another user has a note for note security
    When I update that note with my token
    Then I should receive a 403 status code for note security

  Scenario: Prevent deleting another user's note
    Given the API is running
    And another user has a note for note security
    When I delete that note with my token
    Then I should receive a 403 status code for note security

  Scenario: Allow a user to retrieve their own note
    Given the API is running
    And I have a note for note security
    When I retrieve that note with my token
    Then I should receive a 200 status code for note security
