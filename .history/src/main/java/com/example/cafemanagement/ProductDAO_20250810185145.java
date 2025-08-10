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

public ProductDAO() {
    coll = MongoDBUtil.getDatabase().getCollection("products");

    // Ensure unique compound index on (name + category)
    IndexOptions options = new IndexOptions().unique(true);

    try {
        String indexName = coll.createIndex(Indexes.compoundIndex(
            Indexes.ascending("name"),
            Indexes.ascending("category")
        ), options);
        System.out.println("Created index: " + indexName);
    } catch (Exception e) {
        System.err.println("Failed to create index: " + e.getMessage());
    }
}

    /**
     * Insert a new product only if no product with the same name and category exists.
     * Returns the inserted product with ID if success, otherwise null.
     */
   public Product insert(Product p) {
    System.out.println("Attempting to insert product: " + p.getName() + ", " + p.getCategory());
    try {
        Document doc = new Document("name", p.getName())
                .append("price", p.getPrice())
                .append("category", p.getCategory())
                .append("imagePath", p.getImagePath());
        coll.insertOne(doc);
        String id = doc.getObjectId("_id").toHexString();
        p.setId(id);
        System.out.println("Insert successful for product: " + p.getName());
        return p;
    } catch (MongoWriteException e) {
        if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
            System.out.println("Duplicate detected for product: " + p.getName());
            return null;
        }
        throw e;
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
