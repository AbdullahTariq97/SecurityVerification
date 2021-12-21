package com.sky.security.service.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Slf4j
public class Client {

    @Value(value="${wiremock.host}")
    private String host;

    @Value("${wiremock.port}")
    private String port;

    public HttpResponse<String> sendGetRequest(String endPoint)  {

        String url = UriComponentsBuilder.fromUriString(host).path(endPoint).port(port).build().toUriString();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
