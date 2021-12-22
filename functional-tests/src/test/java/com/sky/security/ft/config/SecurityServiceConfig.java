package com.sky.security.ft.config;

import com.sky.security.service.SecurityServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class SecurityServiceConfig {

    private ConfigurableApplicationContext configurableApplicationContext;

    @PostConstruct
    public void startupSecurityService(){
        configurableApplicationContext = SpringApplication.run(SecurityServiceApplication.class);
    }

    @PreDestroy
    public void stopFodmapService(){
        configurableApplicationContext.close();
    }
}
