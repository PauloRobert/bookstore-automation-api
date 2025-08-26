package com.demoqa.services;

import com.demoqa.utils.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.*;

public class BookService {

    public BookService() {
        RestAssured.baseURI = ConfigManager.get("base.url");
    }

    public Response listBooks() {
        return RestAssured.given()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .get("/BookStore/v1/Books");
    }

    public Response addBooksToUser(String userId, List<String> isbns, String bearerToken) {
        List<Map<String, String>> collection = new ArrayList<>();
        for (String isbn : isbns) {
            Map<String, String> item = new HashMap<>();
            item.put("isbn", isbn);
            collection.add(item);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("collectionOfIsbns", collection);

        return RestAssured.given()
                .header("Authorization", "Bearer " + bearerToken)
                .header("Accept", "application/json")
                .contentType("application/json")
                .body(body)
                .post("/BookStore/v1/Books");
    }
}