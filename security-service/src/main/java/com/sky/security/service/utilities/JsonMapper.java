package com.sky.security.service.utilities;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonMapper {

    public static String readFileFromResources(Class className, String filename) throws URISyntaxException, IOException {
        URL resource = className.getClassLoader().getResource(filename);
        byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));
        return new String(bytes);
    }


}
