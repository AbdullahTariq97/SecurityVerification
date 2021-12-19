package com.sky.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sky.security.service.Controllers.VerificationController;
import com.sky.security.service.Exceptions.DownstreamException;
import com.sky.security.service.Models.Crime;
import com.sky.security.service.Models.CrimeNature;
import com.sky.security.service.Models.Criminal;
import com.sky.security.service.Models.Person;
import com.sky.security.service.Services.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// What do the dependencies have to return in order for the class you are testing to return what you want it to

@ExtendWith(MockitoExtension.class)
public class VerificationControllerTest {

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private VerificationController verificationController;

    @Test
    public void givenThereIsAMatchIsFound_shouldReturnDetailsOfTheCriminal() throws Exception {
        Person person = new Person("Jack","Ripper","1234",43);
        List<Crime> crimeList = Arrays.asList(new Crime("1884-07-13", CrimeNature.MURDER.toString()), new Crime("1885-08-14",CrimeNature.MURDER.toString()));
        Criminal criminal = new Criminal("Jack","Ripper", 43,crimeList);

        when(verificationService.checkPerson(person)).thenReturn(true);
        when(verificationService.getCriminal()).thenReturn(criminal);

        ResponseEntity response = verificationController.verifyIndividual(person);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).extracting("firstName","lastName","crimes").containsExactly("Jack","Ripper",crimeList);
    }

    @Test
    public void givenThereIsNoMatchFound_shouldReturnNotFoundInResponseBody() throws Exception {
        Person person = new Person("John","Smith","4567",50);

        when(verificationService.checkPerson(person)).thenReturn(false);

        ResponseEntity response = verificationController.verifyIndividual(person);
        assertThat(response.getBody()).isEqualTo("No Matches Found");
    }

    @Test
    public void givenThatDownstreamHasFailed_shouldThrowDownstreamException() throws Exception {
        Person person = new Person("John","Smith","4567",50);
        when(verificationService.checkPerson(person)).thenThrow(new DownstreamException(400, "Downstream have failed"));

        DownstreamException exceptionThrownByController = assertThrows(DownstreamException.class, () -> verificationController.verifyIndividual(person));
        assertThat(exceptionThrownByController).extracting("statusCode","message").containsExactly(400,"Downstream have failed");
    }

}
