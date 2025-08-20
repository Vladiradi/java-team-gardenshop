# GardenShop
project by **Java Four**
<p align="left">
  <img src="java.png" alt="Java Four Team" width="300 "/>
</p>

### About the Project

**GardenShop** is a backend application developed as a graduation project by the **Java Four**. for an online store specializing
in home and garden products.
It allows customers to browse the catalog, add products to their cart, place orders, and track order statuses.
Administrators can manage the catalog, categories, discounts, and access sales reports for analytics.
---

## Links

- [Technical Task](https://docs.google.com/document/d/1Xn41eFhdYAJVYzRucsNwpbLJ5lNxdvpfx__SZf5DwXA/edit?tab=t.0#heading=h.e2bcw3kuo1da)
- [Layout (Figma)](https://www.figma.com/design/SDNWLzCWkh9ZXdCpWEaByv/project-frontend?node-id=5251-7386&p=f&t=GAPyXsk75XCC4sjs-0)
- [REST API Documentation](https://confirmed-baron-2e5.notion.site/REST-API-f186cf63a46c4020b2237f73093922ab)
- [Deployed Version (Swagger UI)](http://150.241.114.225:8082/swagger-ui/index.html#/)

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

### Vladimir Ryzhov
**Team lead**

**Contributions:**
- Implemented **Order creation and management**
- Refactored **Order logic and controller**
- Implemented **Scheduler** for automatic order status updates
- Integrated **JWT authentication**
- Developed **RBAC (ADMIN/USER) system**
- Built **CRUD layers**: Repository, Service, Controller, DTO
- Configured **Liquibase migrations and foreign keys**
- Set up **Spring Boot project** and dependencies
- Implemented **DTO validation and converters**
- Added **Product of the Day** feature

---

### Arcady Zon
**Developer**

**Contributions:**
- Implemented **Global Exception Handling** with `GlobalExceptionHandler`
- Built **Payment Service**: create & update payments
- Implemented **Product filtering and sorting**
- Developed **CartItem management**: add/remove products in cart
- Refactored `PaymentService` and unified code style across all layers
- Integrated **JaCoCo** for test coverage validation**

---

### Liudmyla Iermolenko
**Developer**

**Contributions:**
- Implemented **Favorite entity with CRUD logic**
- Developed **User profile editing (PUT)**
- Implemented **Product editing (PUT)**
- Added **OrderItem** to store product details in orders
- Built **Report Service & Report Controller**
- Developed **Reporting Module** with analytics features
- Optimized **transactional usage** across services
- Rebuilt and improved **ReportService**

### Anna Boldt
**Developer**

**Contributions:**
- Implemented **Category editing**
- Added **Order history**: view past orders
- Developed **Favorites** functionality and fixed null-safe execution
- Implemented **User Cart creation**
- Wrote **unit tests** for category editing, services, and controllers
- Contributed to **team presentation preparation**
