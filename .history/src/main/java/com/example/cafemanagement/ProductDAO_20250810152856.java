package com.example.cafemanagement;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private MongoCollection<Document> collection;

    public ProductDAO() {
        this.collection = MongoDBUtil.getDatabase().getCollection("products");
    }

    public void saveProduct(Product product) {
        Document doc = new Document("name", product.getName())
                .append("price", product.getPrice())
                .append("category", product.getCategory());
        collection.insertOne(doc);
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String name = doc.getString("name");
                double price = doc.getDouble("price");
                String category = doc.getString("category");
                products.add(new Product(name, price, category));
            }
        } finally {
            cursor.close();
        }
        return products;
    }

    public void deleteProduct(String name) {
        collection.deleteOne(new Document("name", name));
    }
}
