Feature: Has the sim card been activated?
  Everybody wants to know if the sim card has been activated.

  Scenario: ICCID "1255789453849037777" is activated
    Given the ICCID number is "1255789453849037777"
    When I ask whether the sim card has been activated
    Then I should be told "Activation Successful"

  Scenario: ICCID "8944500102198304826" has not been activated
    Given the ICCID number is "8944500102198304826"
    When I ask whether the sim card has been activated
    Then I should be told "Not Activated"
