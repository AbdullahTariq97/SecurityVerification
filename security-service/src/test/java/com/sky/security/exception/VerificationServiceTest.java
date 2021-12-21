package com.sky.security.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.security.service.exceptions.DownstreamException;
import com.sky.security.service.models.Crime;
import com.sky.security.service.models.CrimeNature;
import com.sky.security.service.models.Person;
import com.sky.security.service.models.WantedPerson;
import com.sky.security.service.services.VerificationService;
import com.sky.security.service.utilities.Client;
import com.sky.security.service.utilities.JsonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    @Spy
    private ObjectMapper mapper;

    @Mock
    private Client client;

    @InjectMocks
    private VerificationService verificationService;


    @Test
    public void givenDownstreamsAreUp_IfPassedGuiltyPerson_ShouldReturnOptionalWithWantedPersonDetails() throws URISyntaxException, IOException {
        // Given
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
        String happyCaseResponse = JsonMapper.readFileFromResources(VerificationServiceTest.class, "blacklist-responses/happy-case.json");
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(happyCaseResponse);

        when(client.sendGetRequest("/blacklist")).thenReturn(httpResponseMock);


        // When
        Person person = Person.builder().age(26).firstName("Jack").lastName("Ripper").nationalInsuranceNumber(12345).build();
        Optional<WantedPerson> wantedPersonFromService = verificationService.getCriminalRecord(person);

        // Then
        assertThat(wantedPersonFromService.isPresent()).isTrue();

        WantedPerson wantedPerson = WantedPerson.builder().age(26).age(26).firstName("Jack").lastName("Ripper")
                .crimes(List.of(new Crime("1884-07-13", CrimeNature.MURDER.name()),
                        new Crime("1885-08-14", CrimeNature.MURDER.name()))).build();
        assertThat(wantedPersonFromService).usingRecursiveComparison().isEqualTo(wantedPerson);
    }

    @Test
    public void givenDownstreamsAreUp_whenPassedInnocentPerson_shouldReturnEmptyOptional() throws URISyntaxException, IOException {
        // Given
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
        String happyCaseResponse = JsonMapper.readFileFromResources(VerificationServiceTest.class, "blacklist-responses/happy-case.json");
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(happyCaseResponse);

        when(client.sendGetRequest("/blacklist")).thenReturn(httpResponseMock);

        // When
        Person person = Person.builder().age(26).firstName("Mother").lastName("Terresa").nationalInsuranceNumber(678910).build();
        Optional<WantedPerson> wantedPersonFromService = verificationService.getCriminalRecord(person);

        // Then
        assertThat(wantedPersonFromService.isPresent()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {400,500})
    public void givenDownstreamAreDown_whenAGuiltyPersonPassedIn_ShouldThrowDownstreamException(int statusCodeOfDownstream){
        // Given
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
        when(httpResponseMock.statusCode()).thenReturn(statusCodeOfDownstream);
        when(client.sendGetRequest("/blacklist")).thenReturn(httpResponseMock);

        // When
        Person person = Person.builder().age(26).firstName("Jack").lastName("Ripper").nationalInsuranceNumber(12345).build();
        DownstreamException downstreamException = assertThrows(DownstreamException.class, () ->  verificationService.getCriminalRecord(person));
        assertThat(downstreamException).extracting("statusCode","message").containsExactly(statusCodeOfDownstream,"Downstream call has failed");
    }

    @ParameterizedTest
    @ValueSource(ints = {400,500})
    public void givenDownstreamsAreDown_whenPassedAInnocent_shouldThrowDownstreamException(int statusCodeOfDownstream) {
        // Given
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
        when(httpResponseMock.statusCode()).thenReturn(statusCodeOfDownstream);
        when(client.sendGetRequest("/blacklist")).thenReturn(httpResponseMock);

        // When
        Person person = Person.builder().age(26).firstName("Mother").lastName("Terresa").nationalInsuranceNumber(678910).build();
        DownstreamException downstreamException = assertThrows(DownstreamException.class, () ->  verificationService.getCriminalRecord(person));
        assertThat(downstreamException).extracting("statusCode","message").containsExactly(statusCodeOfDownstream,"Downstream call has failed");
    }

    // Further edge cases can be written for this
    @Test
    public void givenObjectMapperFailedToParseDownstreamResponseJson_shouldThrowDownstreamException() throws JsonProcessingException {
        // Given
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(" : value");
        when(client.sendGetRequest("/blacklist")).thenReturn(httpResponseMock);

        // When
        Person person = Person.builder().age(26).firstName("Mother").lastName("Terresa").nationalInsuranceNumber(678910).build();
        DownstreamException downstreamException = assertThrows(DownstreamException.class, () -> verificationService.getCriminalRecord(person));
    }
}

