package com.sky.security.controller;

import com.sky.security.service.controllers.VerificationController;
import com.sky.security.service.models.Crime;
import com.sky.security.service.models.CrimeNature;
import com.sky.security.service.models.Person;
import com.sky.security.service.models.WantedPerson;
import com.sky.security.service.services.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationControllerTest {

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private VerificationController verificationController;

    @Test
    public void givenVerificationReturnsTrue_shouldReturnCriminalDetails(){
        // Given
        Person person = Person.builder().age(26).firstName("Jack").lastName("Ripper").nationalInsuranceNumber(12345).build();
        WantedPerson wantedPersonFromService = WantedPerson.builder().age(26).firstName("Jack").firstName("Ripper")
                .crimes(List.of(new Crime("12-01-1887", CrimeNature.MURDER.name()))).build();

        when(verificationService.getCriminalRecord(person)).thenReturn(Optional.of(wantedPersonFromService));

        // When
        ResponseEntity response = verificationController.verifyIndividual(person);

        // Then
        assertThat(response).extracting("status","body").containsExactly(HttpStatus.OK, wantedPersonFromService);
    }

    @Test
    public void givenVerificationReturnsFalse_shouldReturnAppropriateResponse(){

        // Given
        Person person = Person.builder().age(56).firstName("Mother").lastName("Terresa").nationalInsuranceNumber(5678).build();
        when(verificationService.getCriminalRecord(person)).thenReturn(Optional.empty());

        // When
        ResponseEntity response = verificationController.verifyIndividual(person);

        // Then
        assertThat(response).extracting("status","body").containsExactly(HttpStatus.OK, "No matches found in database");

    }

    @Test
    public void givenClientDoesNotPassInPersonInRequestBody_shouldThrowIllegalArgumentException(){
        // When and then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> verificationController.verifyIndividual(null));
        assertThat(illegalArgumentException).extracting("message").isEqualTo("Pass in person information on which to run background checks");
    }

}
