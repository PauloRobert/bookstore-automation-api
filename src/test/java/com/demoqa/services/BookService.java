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
                .get("/BookStore/v1/Book"); // :contentReference[oaicite:9]{index=9}
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
                .contentType("application/json")
                .body(body)
                .post("/BookStore/v1/Book"); // :contentReference[oaicite:10]{index=10}
    }
}