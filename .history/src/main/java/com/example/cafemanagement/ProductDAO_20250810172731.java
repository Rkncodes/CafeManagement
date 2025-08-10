package com.example.cafemanagement;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final MongoCollection<Document> coll;

    public ProductDAO() {
        coll = MongoDBUtil.getDatabase().getCollection("products");
    }

    public Product insert(Product p) {
        System.out.println("INSERT CALLED for: " + p.getName());
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
    Set<String> seenNames = new HashSet<>();

    try (MongoCursor<Document> it = coll.find().iterator()) {
        while (it.hasNext()) {
            Document d = it.next();
            String id = d.getObjectId("_id").toHexString();
            String name = d.getString("name");

            // Skip if we've already seen this product name
            if (seenNames.contains(name.toLowerCase())) {
                continue;
            }
            seenNames.add(name.toLowerCase());

            Object priceObj = d.get("price");
            double price = (priceObj instanceof Number) ? ((Number) priceObj).doubleValue() : 0.0;

            String category = d.getString("category");
            String imagePath = d.getString("imagePath");
            list.add(new Product(id, name, price, category, imagePath));
        }
    }
    return list;
}


    // simple update by _id (only basic fields)
    public void update(Product p) {
        Document update = new Document("$set", new Document("name", p.getName())
                .append("price", p.getPrice())
                .append("category", p.getCategory())
                .append("imagePath", p.getImagePath()));
        coll.updateOne(new Document("_id", new ObjectId(p.getId())), update);
    }

    public void deleteById(String id) {
        coll.deleteOne(new Document("_id", new ObjectId(id)));
    }
}
