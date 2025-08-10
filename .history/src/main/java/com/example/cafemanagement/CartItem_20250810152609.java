package com.example.cafemanagement;

public class CartItem {
    private String productId;
    private String name;
    private double price;
    private int qty;

    public CartItem(String productId, String name, double price, int qty) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQty() { return qty; }

    public double subtotal() { return price * qty; }
}
