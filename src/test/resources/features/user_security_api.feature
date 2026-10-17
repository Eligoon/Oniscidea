Feature: User API security

  Scenario: User cannot retrieve another user's account
    Given two users exist for user security tests
    When the first user retrieves the second user's account
    Then the user security response status should be 403

  Scenario: User cannot update another user's account
    Given two users exist for user security tests
    When the first user updates the second user's account
    Then the user security response status should be 403

  Scenario: User cannot delete another user's account
    Given two users exist for user security tests
    When the first user deletes the second user's account
    Then the user security response status should be 403

  Scenario: User can only retrieve their own account from the users list
    Given two users exist for user security tests
    When the first user retrieves the users list
    Then the user security response status should be 200
    And the users list should contain only the first user
