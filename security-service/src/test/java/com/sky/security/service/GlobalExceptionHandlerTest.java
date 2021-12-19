package com.sky.security.service;

import com.sky.security.service.Controllers.VerificationController;
import com.sky.security.service.Exceptions.DownstreamException;
import com.sky.security.service.Exceptions.ErrorDto;
import com.sky.security.service.Exceptions.GlobalExceptionHandler;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.regex.Matcher;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private VerificationController verificationController;


    @BeforeEach
    public void setMockMvc(){
        mockMvc = MockMvcBuilders.standaloneSetup(verificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void givenDownstreamExceptionPassed_shouldReturnErrorDTO(){
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        DownstreamException downstreamException = new DownstreamException(400,"Downstream have failed");
        ResponseEntity<ErrorDto> responseEntity = globalExceptionHandler.handleDownstreamException(downstreamException);
        assertThat(responseEntity.getBody()).extracting("statusCode","errorCode","message").containsExactly(500, "VR101","Downstream have failed");

    }

    // Test below check if the downstream exception thrown by a controller are caught by the global exception handler
    @Test
    public void givenDownstreamAreNotHealthy_AndDownstreamExceptionThrown_shouldReturnErrorDTO() throws Exception {
        when(verificationController.verifyIndividual(any())).thenThrow(DownstreamException.class);
        mockMvc.perform(get("/verify"))
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.statusCode", Matchers.is(500)))
                .andExpect(jsonPath("$.errorCode", Matchers.is("VR101")))
                .andExpect(jsonPath("$.message", Matchers.is("Downstream have failed")))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(DownstreamException.class));
    }

    @Test
    public void givenDownstreamFailedToConnect_AndDownstreamExceptionThrown_shouldReturnErrorDTO() throws Exception {
        mockMvc.perform(get("/verify"))
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.statusCode", Matchers.is(500)))
                .andExpect(jsonPath("$.errorCode", Matchers.is("VR101")))
                .andExpect(jsonPath("$.message", Matchers.is("Downstream have failed")))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(DownstreamException.class));

    }
}
