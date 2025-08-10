package com.example.cafemanagement;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

public class ProductDAO {
    private final MongoCollection<Document> coll;

    public ProductDAO() {
        coll = MongoDBUtil.getDatabase().getCollection("products");
        // Ensure unique index on (name + category)
        coll.createIndex(new Document("name", 1).append("category", 1),
                         new com.mongodb.client.model.IndexOptions().unique(true));
    }

    // Insert product - returns true if inserted, false if duplicate key error
    public boolean insert(Product p) {
        try {
            Document doc = new Document("name", p.getName())
                    .append("price", p.getPrice())
                    .append("category", p.getCategory())
                    .append("imagePath", p.getImagePath());
            coll.insertOne(doc);
            p.setId(doc.getObjectId("_id").toHexString());
            return true;
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                return false; // duplicate detected
            }
            throw e; // rethrow if other error
        }
    }

    // Update product - returns true if updated, false if duplicate key error or no modification
    public boolean update(Product p) {
        try {
            Document update = new Document("$set", new Document("name", p.getName())
                    .append("price", p.getPrice())
                    .append("category", p.getCategory())
                    .append("imagePath", p.getImagePath()));
            UpdateResult result = coll.updateOne(new Document("_id", new ObjectId(p.getId())), update);
            return result.getModifiedCount() > 0;
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                return false; // duplicate detected on update
            }
            throw e;
        }
    }

    // Find all unique products by name + category (filter duplicates in memory)
    public List<Product> findAll() {
        List<Product> result = new ArrayList<>();
        Set<String> uniqueKeys = new HashSet<>();
        for (Document doc : coll.find()) {
            String key = doc.getString("name").toLowerCase() + "|" + doc.getString("category").toLowerCase();
            if (uniqueKeys.contains(key)) continue; // skip duplicate
            uniqueKeys.add(key);
            Product p = new Product();
            p.setId(doc.getObjectId("_id").toHexString());
            p.setName(doc.getString("name"));
            p.setPrice(doc.getDouble("price"));
            p.setCategory(doc.getString("category"));
            p.setImagePath(doc.getString("imagePath"));
            result.add(p);
        }
        return result;
    }

    // Delete product by id
    public boolean deleteById(String id) {
        return coll.deleteOne(new Document("_id", new ObjectId(id))).getDeletedCount() > 0;
    }
}
