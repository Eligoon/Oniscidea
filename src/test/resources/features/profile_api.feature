Feature: Profile API

  Scenario: Create a profile

    Given the API is running
    And I create a user for the profile
    When I create a profile with name "My Profile"
    Then I should receive a 201 status code
    And the response should contain the profile name "My Profile"