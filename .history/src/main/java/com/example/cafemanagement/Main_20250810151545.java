package com.example.cafemanagement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class Main {
    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {
            System.out.println("✅ Connected to MongoDB!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
