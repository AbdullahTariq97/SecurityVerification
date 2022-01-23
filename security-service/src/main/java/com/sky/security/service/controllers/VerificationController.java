package com.sky.security.service.controllers;

import com.sky.security.service.models.Person;
import com.sky.security.service.models.WantedPerson;
import com.sky.security.service.services.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    // set required to false to draft custom bad request response
    @PostMapping("/verify")
    public ResponseEntity verifyIndividual(@RequestBody(required = false) Person person) {

        Person personFromOptional = Optional.ofNullable(person)
                .orElseThrow(() -> new IllegalArgumentException("Pass in person information on which to run background checks"));

        Optional<WantedPerson> wantedPersonRecord = verificationService.getCriminalRecord(personFromOptional);

        return wantedPersonRecord.isPresent() ? ResponseEntity.ok(wantedPersonRecord.get()) : ResponseEntity.status(HttpStatus.NOT_FOUND).body("No matches found in database");
    }
}
