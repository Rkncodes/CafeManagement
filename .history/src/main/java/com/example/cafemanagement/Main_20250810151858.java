package com.example.cafemanagement;

public class Main {
    public static void main(String[] args) {
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");

        ProductDAO productDAO = new ProductDAO();
        productDAO.addProduct(new Product("Cappuccino", 120.0, "Beverage"));

        MongoDBUtil.close();
    }
}
