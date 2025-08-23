package com.demoqa.services;

import com.demoqa.models.UserCredentials;
import com.demoqa.utils.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    public UserService() {
        RestAssured.baseURI = ConfigManager.get("base.url");
    }

    public String createUser(UserCredentials creds) {
        Map<String, Object> body = new HashMap<>();
        body.put("userName", creds.getUserName());
        body.put("password", creds.getPassword());

        Response resp = RestAssured.given()
                .contentType("application/json")
                .body(body)
                .post("/Account/v1/User"); // :contentReference[oaicite:5]{index=5}

        // Em caso de erro, deixar mensagem clara
        if (resp.statusCode() != 201 && resp.statusCode() != 200) {
            throw new IllegalStateException("Falha ao criar usuário: " + resp.statusCode() + " - " + resp.asString());
        }
        return resp.jsonPath().getString("userID");
    }

    public String generateToken(UserCredentials creds) {
        Response resp = RestAssured.given()
                .contentType("application/json")
                .body(creds)
                .post("/Account/v1/GenerateToken"); // :contentReference[oaicite:6]{index=6}

        if (resp.statusCode() != 200) {
            throw new IllegalStateException("Falha ao gerar token: " + resp.statusCode() + " - " + resp.asString());
        }
        return resp.jsonPath().getString("token");
    }

    public boolean isAuthorized(UserCredentials creds) {
        Response resp = RestAssured.given()
                .contentType("application/json")
                .body(creds)
                .post("/Account/v1/Authorized"); // :contentReference[oaicite:7]{index=7}
        // API retorna true/false no body
        return "true".equalsIgnoreCase(resp.asString()) || resp.jsonPath().getBoolean("");
    }

    public Response getUser(String userId, String bearerToken) {
        return RestAssured.given()
                .header("Authorization", "Bearer " + bearerToken)
                .get("/Account/v1/User/{UUID}", userId); // :contentReference[oaicite:8]{index=8}
    }
}