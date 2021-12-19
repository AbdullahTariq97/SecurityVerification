package com.sky.security.service.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.security.service.Exceptions.DownstreamException;
import com.sky.security.service.Models.Criminal;
import com.sky.security.service.Models.Person;
import com.sky.security.service.Utilities.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class VerificationService {

    @Autowired
    private Client client;

    private Optional<Criminal> criminal = null;

    public boolean checkPerson(Person person) throws DownstreamException {

        Optional<HttpResponse<String>> jsonResponse = Optional.ofNullable(client.sendGetRequest("/blacklist"));
        if(jsonResponse.isPresent()) {

            if (HttpStatus.valueOf(jsonResponse.get().statusCode()).is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();

                try {

                    Map<String, List<Criminal>> javaDataStructureResponse = mapper.readValue(jsonResponse.get().body(), new TypeReference<Map<String, List<Criminal>>>() {
                    });
                    for (Map.Entry<String, List<Criminal>> set : javaDataStructureResponse.entrySet()) {
                        Optional<Criminal> match = set.getValue().stream()
                                .filter(criminal -> criminal.getFirstName().equalsIgnoreCase(person.getFirstName()))
                                .filter(criminal -> criminal.getLastName().equalsIgnoreCase(person.getLastName())).findFirst();
                        if (match.isPresent()) {
                            setCriminal(match);
                            return true;
                        }
                    }
                    return false;

                } catch (JsonProcessingException e) {
                    log.error(e.getMessage());
                }
            }
            throw new DownstreamException(jsonResponse.get().statusCode(), "Downstream have failed");
        }
        throw new DownstreamException("Downstream have failed");
    }

    public Criminal getCriminal() {
        return criminal.get();
    }

    public void setCriminal(Optional<Criminal> criminal) {
        this.criminal = criminal;
    }
}
