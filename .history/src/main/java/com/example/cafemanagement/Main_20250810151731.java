package com.example.cafemanagement;

public class Main {
    public static void main(String[] args) {
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");
        System.out.println("✅ MongoDB connected!");

        // Close when done
        MongoDBUtil.close();
    }
}
