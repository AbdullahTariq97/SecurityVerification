package com.sky.security.service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.security.service.exceptions.DownstreamException;
import com.sky.security.service.models.WantedPerson;
import com.sky.security.service.models.Person;
import com.sky.security.service.utilities.Client;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class VerificationService {

    @Autowired
    private Client client;

    @Autowired
    private ObjectMapper mapper;

    @Setter
    private Optional<WantedPerson> wantedPerson;

    public Optional<WantedPerson> getCriminalRecord(Person person) {

        HttpResponse<String> httpResponse = client.sendGetRequest("/blacklist");

        if (HttpStatus.valueOf(httpResponse.statusCode()).is2xxSuccessful()) {
            try {
                Map<String, List<WantedPerson>> response = mapper.readValue(httpResponse.body(), new TypeReference<>() {});

                for (Map.Entry<String, List<WantedPerson>> set : response.entrySet()) {

                    List<WantedPerson> wantedPersonsList = set.getValue();

                    wantedPerson = wantedPersonsList.stream().filter((wantedPerson) -> wantedPerson.getAge() == person.getAge() &&
                            wantedPerson.getFirstName().equalsIgnoreCase(person.getFirstName()) &&
                            wantedPerson.getLastName().equalsIgnoreCase(person.getLastName())).findFirst();
                    if (wantedPerson.isPresent()) {
                        break;
                    }
                }
                return wantedPerson;
            } catch (JsonProcessingException processingException){
                throw new DownstreamException(httpResponse.statusCode(), "Downstream call has failed");
            }
        }
        else {
            throw new DownstreamException(httpResponse.statusCode(),"Downstream call has failed");
        }
    }
}
