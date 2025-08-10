package com.example.cafemanagement;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final MongoCollection<Document> collection;

    public ProductDAO() {
        MongoDatabase db = MongoDBUtil.getDatabase();
        collection = db.getCollection("products");
    }

    public Product insert(Product p) {
        Document doc = new Document("name", p.getName())
                .append("price", p.getPrice())
                .append("category", p.getCategory())
                .append("stock", p.getStock());
        collection.insertOne(doc);
        String id = doc.getObjectId("_id").toHexString();
        p.setId(id);
        return p;
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        for (Document d : collection.find()) {
            list.add(fromDoc(d));
        }
        return list;
    }

    public void update(Product p) {
        Document update = new Document("$set", new Document("name", p.getName())
                .append("price", p.getPrice())
                .append("category", p.getCategory())
                .append("stock", p.getStock()));
        collection.updateOne(Filters.eq("_id", new ObjectId(p.getId())), update);
    }

    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
    }

    public Product findById(String id) {
        Document d = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return d == null ? null : fromDoc(d);
    }

    private Product fromDoc(Document d) {
        Product p = new Product();
        p.setId(d.getObjectId("_id").toHexString());
        p.setName(d.getString("name"));
        p.setPrice(d.getDouble("price") != null ? d.getDouble("price") : d.get("price", Number.class).doubleValue());
        p.setCategory(d.getString("category"));
        p.setStock(d.getInteger("stock", 0));
        return p;
    }

    // decrement stock by qty, returns true if updated (sufficient stock)
    public boolean decrementStockAtomic(String id, int qty) {
        Document res = collection.findOneAndUpdate(
            Filters.and(Filters.eq("_id", new ObjectId(id)), Filters.gte("stock", qty)),
            new Document("$inc", new Document("stock", -qty))
        );
        return res != null;
    }
}
