package com.github.geovanegsfarias.configuration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@TestConfiguration
@Lazy
public class RestAssuredConfig {

    @Bean
    public RequestSpecification requestSpecificationRegularUser() {
        return RestAssured.given()
                .baseUri("http://localhost")
                .contentType(ContentType.JSON);
    }

}
