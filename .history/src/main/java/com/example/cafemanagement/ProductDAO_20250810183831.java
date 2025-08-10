package com.example.cafemanagement;

import com.mongodb.MongoWriteException;
import com.mongodb.ErrorCategory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductDAO {
    private final MongoCollection<Document> coll;

    public ProductDAO() {
        coll = MongoDBUtil.getDatabase().getCollection("products");

        // Ensure unique compound index on (name + category)
        IndexOptions options = new IndexOptions().unique(true);
        coll.createIndex(Indexes.compoundIndex(
                Indexes.ascending("name"),
                Indexes.ascending("category")
        ), options);
    }

    /**
     * Check if a product with the same name and category already exists in the DB.
     */
    public boolean existsByNameAndCategory(String name, String category) {
        Document query = new Document("name", name)
                .append("category", category);
        return coll.find(query).first() != null;
    }

    /**
     * Insert a new product only if no product with the same name and category exists.
     * Returns the inserted product with ID if success, otherwise null.
     */
    public Product insert(Product p) {
        if (existsByNameAndCategory(p.getName(), p.getCategory())) {
            // Product already exists, so don't insert
            return null;
        }
        try {
            Document doc = new Document("name", p.getName())
                    .append("price", p.getPrice())
                    .append("category", p.getCategory())
                    .append("imagePath", p.getImagePath());
            coll.insertOne(doc);
            String id = doc.getObjectId("_id").toHexString();
            p.setId(id);
            return p;
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                // Duplicate found, return null or handle as needed
                return null;
            }
            throw e; // unexpected error
        }
    }

    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        try (MongoCursor<Document> it = coll.find().iterator()) {
            while (it.hasNext()) {
                Document d = it.next();
                String id = d.getObjectId("_id").toHexString();
                String name = d.getString("name");
                String category = d.getString("category");
                String key = name.toLowerCase() + "::" + category.toLowerCase();

                if (seen.contains(key)) {
                    continue;
                }
                seen.add(key);

                Object priceObj = d.get("price");
                double price = (priceObj instanceof Number) ? ((Number) priceObj).doubleValue() : 0.0;
                String imagePath = d.getString("imagePath");

                list.add(new Product(id, name, price, category, imagePath));
            }
        }
        return list;
    }

    public boolean update(Product p) {
        Document update = new Document("$set", new Document("name", p.getName())
                .append("price", p.getPrice())
                .append("category", p.getCategory())
                .append("imagePath", p.getImagePath()));
        UpdateResult result = coll.updateOne(new Document("_id", new ObjectId(p.getId())), update);
        return result.getModifiedCount() > 0;
    }

    public boolean deleteById(String id) {
        DeleteResult result = coll.deleteOne(new Document("_id", new ObjectId(id)));
        return result.getDeletedCount() > 0;
    }
}
