package com.sky.security.ft;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = "html:reports/report.html",
        glue = "com.sky.security.ft",
        features = "src/test/resources/features",
        tags = "@my-test"
        )
public class CucumberTestRunner {

}
