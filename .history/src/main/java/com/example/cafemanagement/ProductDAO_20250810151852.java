package com.example.cafemanagement;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class ProductDAO {
    private final MongoCollection<Document> collection;

    public ProductDAO() {
        MongoDatabase db = MongoDBUtil.getDatabase();
        collection = db.getCollection("products");
    }

    public void addProduct(Product product) {
        Document doc = new Document("name", product.getName())
                .append("price", product.getPrice())
                .append("category", product.getCategory());
        collection.insertOne(doc);
        System.out.println("✅ Product added: " + product.getName());
    }
}
