Feature: should verify person passed in
  @my-test
  Scenario: given downstreams are up, if guilty person passed in, should return wanted persons details
    Given that the downstream "blacklist" is healthy
    When the "/verify" endpoint is polled with request body:
    | firstName               | Jack   |
    | lastName                | Ripper |
    | age                     | 43     |
    | nationalInsuranceNumber | 12345  |
    Then status code of 200 should be returned
    And the service should return response body matching file "wanted_person_details.json"


  Scenario: given downstreams are up, if innocent person passed in, should return appropriate response
    Given that the downstream "blacklist" is healthy
    When the "/verify" endpoint is polled with request body:
      | firstName               | Mother   |
      | lastName                | Terressa |
      | age                     | 77       |
      | nationalInsuranceNumber | 65789    |
    Then status code of 404 should be returned
    And the service should return response body matching "No matches found in database"


  Scenario: given downstreams are down, if guilty person passed in, should return appropriate response
    Given that the downstream "blacklist" is unhealthy
    When the "/verify" endpoint is polled with request body:
      | firstName               | Jack   |
      | lastName                | Ripper |
      | age                     | 43     |
      | nationalInsuranceNumber | 12345  |
    Then status code of 500 should be returned
    And the service should return response body with the following keys and values:
    | downstreamStatusCode | 500                        |
    | errorCode            | VR101                      |
    | message              | Downstream call has failed |


  Scenario: given downstreams are down, if innocent person passed in, should return appropriate response
    Given that the downstream "blacklist" is unhealthy
    When the "/verify" endpoint is polled with request body:
      | firstName               | Mother   |
      | lastName                | Terressa |
      | age                     | 77       |
      | nationalInsuranceNumber | 65789    |
    Then status code of 500 should be returned
    And the service should return response body with the following keys and values:
      | downstreamStatusCode | 500                        |
      | errorCode            | VR101                      |
      | message              | Downstream call has failed |

  Scenario: given that no person is passed in request message, should return appropriate response
    Given that the downstream "blacklist" is healthy
    When the "/verify" endpoint is polled without request body
    Then status code of 400 should be returned
    And the service should return response body matching "Pass in person information on which to run background checks"

  Scenario: given that no person is passed in request message, should return appropriate response
    Given that the downstream "blacklist" is unhealthy
    When the "/verify" endpoint is polled without request body
    Then status code of 400 should be returned
    And the service should return response body matching "Pass in person information on which to run background checks"
