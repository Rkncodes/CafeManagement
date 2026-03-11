☕ Java Cafe POS System

A desktop Point-of-Sale (POS) application built with **Java Swing** to manage cafe operations such as menu management, order processing, and revenue tracking.

The system uses **MySQL for persistent storage** and supports role-based access control for different staff members.

---

## Overview

This application simulates a cafe ordering system where staff can:

- manage menu items
- process customer orders
- track revenue statistics
- control system access through role-based permissions

The goal is to provide a simple POS workflow with a responsive desktop interface.

---

## Key Functional Modules

### 🔐 Authentication & Staff Roles

The system includes a secure login mechanism with restricted access based on roles.

- Passwords stored using **BCrypt hashing**
- Two staff roles supported:

| Role    | Permissions                                              |
|---------|----------------------------------------------------------|
| Admin   | Full system access including product and user management |
| Cashier | Order creation and checkout only                         |

Default accounts are automatically created on the first run.

---

### 🛍️ Menu Management

Admins can maintain the cafe menu through a dedicated panel.

Features include:

- Add new products
- Edit or delete existing items
- Assign product categories (Coffee, Snacks, Dessert, Beverage)
- Upload product images using drag-and-drop
- Display products as interactive card components

---

### 🛒 Order Processing

Cashiers can create and manage orders through the POS interface.

Workflow:

1. Browse or search products  
2. Add items to the cart  
3. Adjust product quantities  
4. Assign a table number  
5. Checkout and generate invoice  

The system automatically applies a **10% tax calculation**.

---

### 📊 Sales Analytics

The analytics module allows admins to monitor business performance.

Available metrics include:

- total revenue
- number of orders
- average order value

Reports can be filtered by:

- Today
- This Week
- This Month
- All Time

---

### 🎨 User Interface

The UI is built using **Java Swing with FlatLaf styling** and includes:

- modern card-based layouts
- dark / light theme toggle
- hover animations
- responsive product browsing panels

---

## Technology Stack

| Component             | Technology |
|-----------------------|------------|
| Language              | Java 17    |
| UI Framework          | Java Swing |
| Database              | MySQL      |
| Database Connectivity | JDBC       |
| Security              | BCrypt     |
| UI Theme              | FlatLaf    |
| Build Tool            | Maven      |

---

## Running the Application

### 1️⃣ Start MySQL

Ensure the **MySQL server is running locally** and the database schema has been created.

### 2️⃣ Run the Project

```
mvn clean compile exec:java -Dexec.mainClass="com.example.cafemanagement.ModernCafeUI"
```

---

## Project Structure

```
src/main/java/com/example/cafemanagement/

ModernCafeUI.java
LoginDialog.java
UserDAO.java
ProductDAO.java
CartManager.java
DatabaseUtil.java
ProductFormDialog.java
ProductEditDialog.java
WrapLayout.java
```

The project follows a **DAO-based architecture**, where database operations are handled through dedicated data access classes.

---

## Database Tables

| Table       | Purpose                        |
|-------------|--------------------------------|
| users       | staff accounts and credentials |
| products    | product catalog                |
| orders      | order records                  |
| order_items | items within each order        |
| analytics   | revenue and order statistics   |

---

## Planned Improvements

Possible future enhancements:

- receipt / invoice printing
- inventory management
- employee shift tracking
- customer loyalty programs
- advanced reporting and exports

---

## Notes

Default login credentials are included for **testing purposes only** and should be changed before production deployment.
