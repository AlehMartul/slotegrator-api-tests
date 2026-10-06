package com.slotegrator.api.client;

import com.slotegrator.api.dto.CredentialsDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AuthClient {

    private final RequestSpecification requestSpec;

    public AuthClient(String baseUrl, String basicUsername, String basicPassword) {
        this.requestSpec = RestAssured.given()
                .baseUri(baseUrl)
                .auth().preemptive().basic(basicUsername, basicPassword)
                .contentType(ContentType.JSON);
    }

    public Response login(CredentialsDTO credentials) {
        return RestAssured.given()
                .spec(requestSpec)
                .body(credentials)
                .post("/api/tester/login");
    }
}
