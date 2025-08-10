package com.example.cafemanagement;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import org.bson.types.ObjectId;

public class OrderDAO {
    private final MongoCollection<Document> coll;
    private final ProductDAO productDAO;

    public OrderDAO() {
        MongoDatabase db = MongoDBUtil.getDatabase();
        coll = db.getCollection("orders");
        productDAO = new ProductDAO();
    }

    // items: list of (productId, name, price, qty, subtotal)
    public void createOrder(List<Document> items, double total) {
        Document order = new Document("items", items)
                .append("total", total)
                .append("createdAt", new Date());
        coll.insertOne(order);
    }

    // place order: check/decrement stock for each item, return true if successful
    public boolean placeOrderAndSave(List<CartItem> cart) {
        // first, try to decrement stock atomically for each product
        for (CartItem it : cart) {
            boolean ok = productDAO.decrementStockAtomic(it.getProductId(), it.getQty());
            if (!ok) {
                // rollback: for simplicity, increment back the ones we already decreased
                for (CartItem prev : cart) {
                    if (prev == it) break;
                    // increment back
                    coll.getDatabase().getCollection("products")
                        .updateOne(new Document("_id", new ObjectId(prev.getProductId())),
                                   new Document("$inc", new Document("stock", prev.getQty())));
                }
                return false;
            }
        }

        // prepare order items
        List<Document> items = new ArrayList<>();
        double total = 0.0;
        for (CartItem it : cart) {
            double subtotal = it.getPrice() * it.getQty();
            total += subtotal;
            items.add(new Document("productId", new ObjectId(it.getProductId()))
                    .append("name", it.getName())
                    .append("price", it.getPrice())
                    .append("qty", it.getQty())
                    .append("subtotal", subtotal));
        }

        createOrder(items, total);
        return true;
    }
}
