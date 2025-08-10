package com.example.cafemanagement;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

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

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        for (Document doc : collection.find()) {
            String name = doc.getString("name");
            double price = doc.getDouble("price");
            String category = doc.getString("category");
            products.add(new Product(name, price, category));
        }
        return products;
    }
}
