package com.slotegrator.api.client;

import com.slotegrator.api.dto.PlayerRequestDTO;
import com.slotegrator.api.dto.PlayerRequestOneDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class PlayersClient {

    private final RequestSpecification requestSpec;

    public PlayersClient(String baseUrl, String accessToken) {
        this.requestSpec = RestAssured.given()
                .baseUri(baseUrl)
                .auth().oauth2(accessToken)
                .contentType(ContentType.JSON);
    }

    public Response createPlayer(PlayerRequestDTO player) {
        return RestAssured.given()
                .spec(requestSpec)
                .body(player)
                .post("/api/automationTask/create");
    }

    public Response getOne(PlayerRequestOneDTO player) {
        return RestAssured.given()
                .spec(requestSpec)
                .body(player)
                .post("/api/automationTask/getOne");
    }

    public Response getAll() {
        return RestAssured.given()
                .spec(requestSpec)
                .get("/api/automationTask/getAll");
    }

    public Response deleteOne(String id) {
        return RestAssured.given()
                .spec(requestSpec)
                .delete("/api/automationTask/deleteOne/" + id);
    }
}