# GardenShop

A graduation project by **Java Team**

---

## About the Project

**GardenShop** is a backend application for an online store specializing in home and garden products.
It allows customers to browse the catalog, add products to their cart, place orders, and track order statuses.
Administrators can manage the catalog, categories, discounts, and access sales reports for analytics.

---

## Useful Links
- [Mockup (Figma)](https://www.figma.com/design/SDNWLzCWkh9ZXdCpWEaByv/project-frontend?node-id=0-1&p=f)
- Demo (coming soon)

---

## Core Features
- **User Management** – registration, authentication, profile editing, account deletion
- **Product & Category Management** – create, update, and delete products and categories (admin only)
- **Advanced Filtering & Sorting** – improved navigation through product catalog
- **Favorites System** – users can add products to favorites
- **Shopping Cart** – add/remove products and manage quantities
- **Order Management** – place orders, update order statuses, view purchase history
- **Payment Service** – create and update payments
- **Discounts & Promotions** – percentage and fixed-price discounts
- **Reporting & Analytics** – sales reports and admin dashboards

---

## Main Functionality

### User
- Register a new account
- Authenticate and log in
- Edit user profile
- Delete account

### Product & Categories
- Add new product
- Edit existing product
- Delete product
- Add new product category
- Edit product category
- Delete product category
- Get list of products
- Get detailed product information
- Get list of product categories

### Cart & Orders
- Add product to cart
- Place an order
- View order status
- View purchase history

### Favorites
- View user’s favorite products

---

## Entities Overview

| Entity     | Description                        |
|------------|------------------------------------|
| **User**   | Manages user accounts              |
| **Category** | Groups products into categories  |
| **Product**  | Stores product details and prices |
| **Favorite** | User favorite products           |
| **Cart**     | Shopping cart                    |
| **CartItem** | Items inside the cart            |
| **Order**    | Customer orders                  |
| **OrderItem**| Items inside an order            |
| **Payment**  | Order payments                   |

---


## Tech Stack

| Technology         | Purpose                               |
|---------------------|---------------------------------------|
| Java 21            | Core programming language             |
| Spring Boot        | Application framework                 |
| Spring Web         | REST API                              |
| Spring Data JPA    | ORM with Hibernate                    |
| PostgreSQL / H2    | Database (production/testing)          |
| Lombok             | Boilerplate code reduction            |
| MapStruct          | DTO ↔ Entity mapping                  |
| Liquibase          | Database migrations & versioning      |
| Spring Security + JWT | Authentication & Authorization     |
| Swagger / OpenAPI  | REST API documentation                |
| JUnit / Mockito    | Testing                               |
| JaCoCo             | Test coverage reports                 |
| Docker             | Containerization & deployment         |
| Git + GitHub       | Version control                       |

---

## Team

### Arcady Zon
**Developer**

**Contributions:**
- Implemented **Product filtering and sorting** (Sprint 2).
- Developed **CartItem management**: add/remove products in cart (Sprint 3).
- Built **Payment Service**: create & update payments (Sprint 3).
- Refactored `PaymentService` and unified code style across all layers.
- Implemented **Global Exception Handling** with `GlobalExceptionHandler` (Sprint 4).
- Integrated **JaCoCo** for test coverage validation.

---
