package com.sky.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.security.service.controllers.VerificationController;
import com.sky.security.service.exceptions.DownstreamException;
import com.sky.security.service.exceptions.ErrorDto;
import com.sky.security.service.exceptions.GlobalExceptionHandler;
import com.sky.security.service.models.Person;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private VerificationController verificationController;

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @BeforeEach
    public void setMockMvc(){
        mockMvc = MockMvcBuilders.standaloneSetup(verificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @ParameterizedTest
    @ValueSource(ints = {400,500})
    public void givenDownstreamExceptionPassed_shouldReturnErrorDTO(int downstreamStatusCode){
        DownstreamException downstreamException = new DownstreamException(downstreamStatusCode,"Downstream call has failed");
        ResponseEntity<ErrorDto> responseEntity = globalExceptionHandler.handleDownstreamException(downstreamException);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseEntity.getBody()).extracting("downstreamStatusCode", "errorCode","message").containsExactly(downstreamException.getStatusCode(), "VR101","Downstream call has failed");

    }

    // Test below check if the downstream exception thrown by a controller are caught by the global exception handler
    @ParameterizedTest
    @ValueSource(ints = {400,500})
    public void givenDownstreamAreNotHealthy_AndDownstreamExceptionThrown_shouldReturnErrorDTO(int downstreamStatusCode) throws Exception {

        when(verificationController.verifyIndividual(any())).thenThrow(new DownstreamException(downstreamStatusCode,"Downstream call has failed"));
        Person person = Person.builder().age(26).firstName("Mother").lastName("Terresa").nationalInsuranceNumber(678910).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String personJson = objectMapper.writeValueAsString(person);
        mockMvc.perform(get("/verify").contentType(MediaType.APPLICATION_JSON).content(personJson))
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.downstreamStatusCode", Matchers.is(downstreamStatusCode)))
                .andExpect(jsonPath("$.errorCode", Matchers.is("VR101")))
                .andExpect(jsonPath("$.message", Matchers.is("Downstream call has failed")))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(DownstreamException.class));
    }

    @Test
    public void givenIllegalArgumentExceptionPassed_shouldReturnAppropriateResponse(){
        IllegalArgumentException iae = new IllegalArgumentException("Pass in person information on which to run background checks");
        ResponseEntity<String> response = globalExceptionHandler.handleIllegalArgumentException(iae);
        assertThat(response).extracting("status","body").containsExactly(400,"Pass in person information on which to run background checks");
    }

    @Test
    public void givenVerifyEndpointPolledWithoutUser_shouldReturnAppropriateResponse() throws Exception {
        when(verificationController.verifyIndividual(null)).thenThrow(new IllegalArgumentException("Pass in person information on which to run background checks"));
        mockMvc.perform(get("/verify"))
                .andExpect(status().is(400))
                .andExpect(content().string("Pass in person information on which to run background checks"));
    }
}
