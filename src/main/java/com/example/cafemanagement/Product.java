package com.example.cafemanagement;

public class Product {
    private String id;          // optional, from Mongo _id hex
    private String name;
    private double price;
    private String category;
    private String imagePath;   // relative path inside resources, e.g. "images/espresso.jpg"

    public Product() {}

    public Product(String name, double price, String category, String imagePath) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    // optional full constructor including id
    public Product(String id, String name, double price, String category, String imagePath) {
        this.id = id; this.name = name; this.price = price; this.category = category; this.imagePath = imagePath;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
