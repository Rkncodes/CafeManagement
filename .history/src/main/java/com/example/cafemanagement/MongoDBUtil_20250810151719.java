package com.example.cafemanagement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtil {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void init(String uri, String dbName) {
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase(dbName);
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("Database not initialized. Call init() first.");
        }
        return database;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
