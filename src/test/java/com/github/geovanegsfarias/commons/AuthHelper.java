package com.github.geovanegsfarias.commons;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.springframework.stereotype.Component;

@Component
public class AuthHelper {
    private final RequestSpecification specification;

    public AuthHelper(RequestSpecification specification) {
        this.specification = specification;
    }

    public String login(String email, String password) {
        return RestAssured.given()
                .spec(specification)
                .auth().preemptive().basic(email, password)
                .post("/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

}