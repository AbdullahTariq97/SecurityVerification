package com.sky.security.ft.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {ComponentSearchConfig.class, SecurityServiceConfig.class},
        initializers = ConfigDataApplicationContextInitializer.class)
@CucumberContextConfiguration
public class CucumberSpringContextConfigration {
}
