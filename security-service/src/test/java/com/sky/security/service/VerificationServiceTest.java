package com.sky.security.service;

import com.sky.security.service.Exceptions.DownstreamException;
import com.sky.security.service.Models.Person;
import com.sky.security.service.Services.VerificationService;
import com.sky.security.service.Utilities.Client;
import com.sky.security.service.Utilities.JsonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {

    @Mock
    private Client client;

    @InjectMocks
    private VerificationService verificationService;

    @Test
    public void givenDownstreamsAreUp_IfPassedGuiltyPerson_ShouldReturnTrue() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
        String happyCaseResponse = JsonMapper.readFileFromResources(VerificationServiceTest.class,"blacklist-responses/happy-case.json");
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(happyCaseResponse);
        when(client.sendGetRequest("/blacklist")).thenReturn(httpResponseMock);
        Person person = new Person("Jack","Ripper","1234",43);
        assertThat(verificationService.checkPerson(person)).isTrue();
    }

    @Test
    public void givenDownstreamsAreUp_whenPassedInnocentPerson_shouldReturnFalse() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
        String happyCaseResponse = JsonMapper.readFileFromResources(VerificationServiceTest.class,"blacklist-responses/happy-case.json");
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(happyCaseResponse);
        when(client.sendGetRequest("/blacklist")).thenReturn(httpResponseMock);
        Person person = new Person("Abdullah","Tariq","2929",24);
        assertThat(verificationService.checkPerson(person)).isFalse();
    }

    @Test
    public void givenDownstreamAreDown_whenAGuiltyPersonPassedIn_ShouldThrowDownstreamException() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
        when(httpResponseMock.statusCode()).thenReturn(400);
        when(client.sendGetRequest("/blacklist")).thenReturn(httpResponseMock);
        Person guiltyPerson = new Person("Jack","Ripper","1234",43);
        DownstreamException downstreamException = assertThrows(DownstreamException.class, () -> verificationService.checkPerson(guiltyPerson));
        assertThat(downstreamException).extracting("statusCode","message").containsExactly(400,"Downstream have failed");
    }

    @Test
    public void givenDownstreamsAreDown_whenPassedAInnocent_shouldThrowDownstreamException() throws IOException, InterruptedException {
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);
        when(httpResponseMock.statusCode()).thenReturn(400);
        when(client.sendGetRequest("/blacklist")).thenReturn(httpResponseMock);
        Person innocentPerson = new Person("Mother","Terresa","24556",80);
        DownstreamException downstreamException = assertThrows(DownstreamException.class, () -> verificationService.checkPerson(innocentPerson));
        assertThat(downstreamException).extracting("statusCode","message").containsExactly(400,"Downstream have failed");
    }

    @Test
    public void givenDownstreamsFailedToConnect_shouldThrowDownstreamException(){
        when(client.sendGetRequest("/blacklist")).thenReturn(null);
        Person guiltyPerson = new Person("Jack","Ripper","1234",43);
        DownstreamException downstreamException = assertThrows(DownstreamException.class,() -> verificationService.checkPerson(guiltyPerson));
        assertThat(downstreamException).extracting("message").isEqualTo("Downstream have failed");
    }
}
