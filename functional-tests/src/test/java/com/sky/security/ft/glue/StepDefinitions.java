package com.sky.security.ft.glue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sky.security.ft.config.CucumberSpringContextConfigration;
import com.sky.security.ft.utility.Client;
import com.sky.security.service.exceptions.ErrorDto;
import com.sky.security.service.models.Person;
import com.sky.security.service.models.WantedPerson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = CucumberSpringContextConfigration.class)
public class StepDefinitions {

    @Autowired
    private Client client;

    private HttpResponse<String> httpResponse;
    private static final int WIREMOCK_PORT = 9000;
    private static final WireMockServer wiremockServer = new WireMockServer(options().port(WIREMOCK_PORT));
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void startupWiremockServer(){
        wiremockServer.start();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
    }

    @PreDestroy
    public void shutDownWiremockServer(){
        wiremockServer.stop();
    }

    @Given("that the downstream {string} is healthy")
    public void that_the_downstream_is_healthy(String string) {
        stubFor(
                get(urlEqualTo("/" + string.toLowerCase()))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type","text/plain")
                                        .withStatus(200)
                                        .withBodyFile("blacklist-app-status.json")
                        )
        );
    }

    @Given("that the downstream {string} is unhealthy")
    public void thatTheDownstreamIsUnhealthy(String string) {
        stubFor(
                get(urlEqualTo("/" + string.toLowerCase()))
                        .willReturn(
                                aResponse()
                                        .withStatus(500)
                        )
        );
    }

    @When("the {string} endpoint is polled with request body:")
    public void theEndpointIsPolledWithRequestBody(String endPoint, Map<String,String> requestBody) throws JsonProcessingException {
        Person person = Person.builder()
                .firstName(requestBody.get("firstName"))
                .lastName(requestBody.get("lastName"))
                .age(Integer.parseInt(requestBody.get("age")))
                .nationalInsuranceNumber(Long.parseLong(requestBody.get("nationalInsuranceNumber"))).build();

        String requestBodyJsonString = objectMapper.writeValueAsString(person);

        httpResponse = client.sendHttpRequest(endPoint,requestBodyJsonString);
    }

    @Then("the service should return response body matching file {string}")
    public void theServiceShouldReturnResponseBodyMatchingFile(String filename) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("expected-mappings/" + filename);
        WantedPerson expectedWantedPerson = objectMapper.readValue(inputStream, WantedPerson.class);

        WantedPerson actualWantedPerson = objectMapper.readValue(httpResponse.body(), WantedPerson.class);

        assertThat(actualWantedPerson).usingRecursiveComparison().isEqualTo(expectedWantedPerson);
    }

    @Then("status code of {int} should be returned")
    public void statusCodeOfShouldBeReturned(int expectedStatusCode) {
        assertThat(httpResponse.statusCode()).isEqualTo(expectedStatusCode);
    }

    @Then("the service should return response body matching {string}")
    public void theServiceShouldReturnResponseBodyMatching(String expectedResponseBody) {
        assertThat(httpResponse.body()).isEqualTo(expectedResponseBody);
    }

    @And("the service should return response body with the following keys and values:")
    public void theServiceShouldReturnResponseBodyWithTheFollowingKeysAndValues(Map<String,String> expectedErrorResponse) throws JsonProcessingException {
        ErrorDto expectedErrorDto = objectMapper.convertValue(expectedErrorResponse, ErrorDto.class);
        ErrorDto actualErrorDto = objectMapper.readValue(httpResponse.body(), new TypeReference<>() {});
        assertThat(actualErrorDto).usingRecursiveComparison().isEqualTo(expectedErrorDto);
    }

    @When("the {string} endpoint is polled without request body")
    public void theEndpointIsPolledWithoutRequestBody(String endPoint) {
        httpResponse = client.sendHttpRequestWithBody(endPoint);
    }
}
