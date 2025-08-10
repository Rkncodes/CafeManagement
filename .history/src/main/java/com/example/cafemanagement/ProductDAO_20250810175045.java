package com.example.cafemanagement;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
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
    }

    public Product insert(Product p) {
        Document existing = coll.find(new Document("name", p.getName()).append("category", p.getCategory())).first();
        if (existing != null) {
            // product already exists with this name + category
            return null;
        }

        Document doc = new Document("name", p.getName())
                .append("price", p.getPrice())
                .append("category", p.getCategory())
                .append("imagePath", p.getImagePath());
        coll.insertOne(doc);
        String id = doc.getObjectId("_id").toHexString();
        p.setId(id);
        return p;
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
