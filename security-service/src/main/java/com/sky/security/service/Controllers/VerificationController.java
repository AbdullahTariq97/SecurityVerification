package com.sky.security.service.Controllers;

import com.sky.security.service.Exceptions.DownstreamException;
import com.sky.security.service.Models.Person;
import com.sky.security.service.Services.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class VerificationController {

    @Autowired
    private VerificationService verificationService;


    @GetMapping("/verify")
    public ResponseEntity verifyIndividual(@RequestBody Person person) throws DownstreamException {
        boolean criminalCheck = false;

        try {
            criminalCheck = verificationService.checkPerson(person);
        } catch (DownstreamException e){
            throw e;
        }

        if(criminalCheck == true){
            return ResponseEntity.ok(verificationService.getCriminal());
        }

        return ResponseEntity.ok("No Matches Found");
    }
}
