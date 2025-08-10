package com.example.cafemanagement;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class CartManager {
    private final Map<Product, Integer> cartItems = new LinkedHashMap<>();
    private String customerName = "";
    private String customerPhone = "";
    private String paymentMethod = "Cash";
    private int tableNumber = 1;

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

    // Calculate tax (assuming 10% tax)
    public double getTax() {
        return getTotal() * 0.10;
    }

    // Calculate grand total
    public double getGrandTotal() {
        return getTotal() + getTax();
    }

    // Clear cart
    public void clearCart() {
        cartItems.clear();
        customerName = "";
        customerPhone = "";
        paymentMethod = "Cash";
        tableNumber = 1;
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    // Customer information
    public void setCustomerInfo(String name, String phone, String paymentMethod, int tableNumber) {
        this.customerName = name != null ? name : "";
        this.customerPhone = phone != null ? phone : "";
        this.paymentMethod = paymentMethod != null ? paymentMethod : "Cash";
        this.tableNumber = tableNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    // Generate invoice text
    public String generateInvoice() {
        StringBuilder invoice = new StringBuilder();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        
        invoice.append("========================================\n");
        invoice.append("           CAFE INVOICE RECEIPT         \n");
        invoice.append("========================================\n");
        invoice.append("Date: ").append(dtf.format(now)).append("\n");
        invoice.append("Table: ").append(tableNumber).append("\n");
        invoice.append("----------------------------------------\n");
        
        if (!customerName.isEmpty()) {
            invoice.append("Customer: ").append(customerName).append("\n");
        }
        if (!customerPhone.isEmpty()) {
            invoice.append("Phone:    ").append(customerPhone).append("\n");
        }
        invoice.append("Payment:  ").append(paymentMethod).append("\n");
        invoice.append("----------------------------------------\n");
        
        invoice.append(String.format("%-25s %5s %10s\n", "ITEM", "QTY", "AMOUNT"));
        invoice.append("----------------------------------------\n");
        
        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            double price = p.getPrice() * qty;
            invoice.append(String.format("%-25s %5d %10s\n", 
                p.getName(), qty, nf.format(price)));
        }
        
        invoice.append("----------------------------------------\n");
        invoice.append(String.format("%-31s %10s\n", "SUBTOTAL:", nf.format(getTotal())));
        invoice.append(String.format("%-31s %10s\n", "TAX (10%):", nf.format(getTax())));
        invoice.append(String.format("%-31s %10s\n", "TOTAL:", nf.format(getGrandTotal())));
        invoice.append("========================================\n");
        invoice.append("        Thank you for your visit!      \n");
        invoice.append("        Please come again soon!         \n");
        invoice.append("========================================\n");
        
        return invoice.toString();
    }
}