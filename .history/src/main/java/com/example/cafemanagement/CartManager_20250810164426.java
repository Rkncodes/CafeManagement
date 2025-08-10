package com.example.cafemanagement;

import java.util.LinkedHashMap;
import java.util.Map;

public class CartManager {
    private final Map<Product, Integer> cartItems = new LinkedHashMap<>();

    // Add product to cart
    public void addProduct(Product product) {
        cartItems.put(product, cartItems.getOrDefault(product, 0) + 1);
    }

    // Remove product completely
    public void removeProduct(Product product) {
        cartItems.remove(product);
    }

    // Update quantity
    public void updateQuantity(Product product, int quantity) {
        if (quantity <= 0) {
            cartItems.remove(product);
        } else {
            cartItems.put(product, quantity);
        }
    }

    // Get all items
    public Map<Product, Integer> getItems() {
        return cartItems;
    }

    // Calculate total price
    public double getTotal() {
        return cartItems.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    // Clear cart
    public void clearCart() {
        cartItems.clear();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}
