# Cafe Management System

A **desktop cafe management application** built with **Java (Swing) and MySQL**, designed to streamline cafe operations including menu management, order processing, billing, and customer feedback.

The system provides a **point-of-sale (POS) style interface** where staff can quickly add items to orders, calculate totals with tax, and manage products through an admin panel.

---

# Tech Stack

* **Java (Swing)** – Desktop GUI
* **MySQL** – Persistent data storage
* **JDBC** – Database connectivity
* **SQL** – Querying and CRUD operations
* **MVC-style modular architecture**

---

# Features

## Menu & Product Management

* Add, edit, and delete cafe menu items
* Categorize items (Coffee, Snacks, Dessert, Beverage)
* Image-based menu display
* Dynamic product loading from the database

---

## Order Management

* Add items to cart with quantity controls
* Live order summary panel
* Real-time subtotal, tax (10%), and total calculation
* Table-based ordering system

---

## Checkout System

* Customer checkout form
* Capture customer name and phone number
* Payment method selection
* Automatic order summary generation

---

## Admin Panel

* Reservation management
* Order status logs
* Customer feedback monitoring
* Ratings and comment tracking

---

## Search & Filtering

* Search products instantly
* Filter menu by category
* Refresh product list dynamically

---

# System Architecture

The application follows a **layered architecture**:

```
UI Layer (Java Swing)
        ↓
Business Logic Layer
        ↓
Database Access Layer (JDBC)
        ↓
MySQL Database
```

This separation improves **maintainability, scalability, and modularity**.

---

# Database Design

Key database tables used in the system:

* `products`
* `orders`
* `order_items`
* `reservations`
* `feedback`

The database stores menu items, customer orders, reservations, and feedback for operational tracking and analysis.

---

# Example Workflow

1. Staff selects menu items from the interface
2. Items are added to the **Order Summary panel**
3. System calculates **subtotal, tax, and total automatically**
4. Customer details are entered during checkout
5. Order is stored in the **MySQL database**
6. Admin panel can review **orders, reservations, and feedback**

---

# Running the Application

Clone the repository and run the project using Maven.

```bash
git clone https://github.com/Rkncodes/CafeManagement.git
cd CafeManagement
mvn clean install 
mvn clean compile exec:java -Dexec.mainClass="com.example.cafemanagement.ModernCafeUI"
```

---

# Future Improvements

* User authentication for admin/staff roles
* Inventory tracking system
* Order history analytics dashboard
* PDF bill generation

---

# Project Preview

The system includes:

* Interactive menu interface
* Real-time order summary panel
* Checkout confirmation window
* Admin dashboard for reservations and feedback

---
## Real-World Use

This system was tested in a local cafe environment to simulate real POS operations including order management, billing, and customer feedback tracking.

