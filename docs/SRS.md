# Software Requirements Specification (SRS)
## Automobile Spare Parts Portal
**Version:** 1.0 | **Date:** October 2026

---

## 1. Introduction

### 1.1 Purpose
This document specifies the requirements for the Automobile Spare Parts Portal — a full-stack web application enabling customers to browse, purchase spare parts and allowing administrators to manage the product catalogue, inventory, and orders.

### 1.2 Scope
- Online spare parts shopping portal
- Secure role-based authentication (Admin / Customer)
- Product catalogue with search, filter, and pagination
- Shopping cart and transactional order processing
- Admin panel for CRUD operations on products, users, and orders
- Full DevOps pipeline (CI/CD, containerization, configuration management)

### 1.3 Definitions
| Term | Definition |
|------|-----------|
| SKU | Stock Keeping Unit — unique product identifier |
| MVP | Minimum Viable Product |
| RBAC | Role-Based Access Control |
| JPA | Java Persistence API |
| CI/CD | Continuous Integration / Continuous Deployment |

---

## 2. System Overview

```
┌─────────────────────────────────────────────────────┐
│                  User Browser                        │
│         (HTML + Bootstrap 5 + JavaScript)           │
└──────────────────┬──────────────────────────────────┘
                   │ HTTP/HTTPS
┌──────────────────▼──────────────────────────────────┐
│           Nginx Reverse Proxy (Port 80)             │
└──────────────────┬──────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────┐
│     Spring Boot Application (Port 8080)             │
│  ┌──────────┐ ┌──────────┐ ┌────────────────────┐  │
│  │Controllers│ │Services  │ │Repositories (JPA)  │  │
│  └──────────┘ └──────────┘ └────────────────────┘  │
│  ┌──────────────────────────────────────────────┐   │
│  │   Spring Security (BCrypt + Session Auth)    │   │
│  └──────────────────────────────────────────────┘   │
└──────────────────┬──────────────────────────────────┘
                   │ JDBC
┌──────────────────▼──────────────────────────────────┐
│           PostgreSQL Database (Port 5432)           │
│  users │ roles │ products │ orders │ cart_items     │
└─────────────────────────────────────────────────────┘
```

---

## 3. Functional Requirements

### 3.1 Authentication (FR-AUTH)
| ID | Requirement |
|----|------------|
| FR-AUTH-01 | System shall authenticate users with username/password |
| FR-AUTH-02 | Passwords shall be encoded with BCrypt (strength 10) |
| FR-AUTH-03 | System shall support two roles: ADMIN, USER |
| FR-AUTH-04 | Failed login shall display a non-revealing error message |
| FR-AUTH-05 | Users shall be able to register with validated data |
| FR-AUTH-06 | Sessions shall be invalidated on logout |
| FR-AUTH-07 | Unauthorized access shall return HTTP 403 |

### 3.2 Product Catalogue (FR-PROD)
| ID | Requirement |
|----|------------|
| FR-PROD-01 | System shall display paginated product catalogue (12/page) |
| FR-PROD-02 | Products shall be searchable by name and description |
| FR-PROD-03 | Products shall be filterable by category |
| FR-PROD-04 | Each product shall have: name, SKU, price, stock, category, brand, image |
| FR-PROD-05 | SKU must be unique across all products |
| FR-PROD-06 | Out-of-stock products shall show visual indicator |

### 3.3 Admin Product Management (FR-ADMIN-PROD)
| ID | Requirement |
|----|------------|
| FR-ADMIN-01 | Admin shall create new products with validated input |
| FR-ADMIN-02 | Admin shall edit existing products |
| FR-ADMIN-03 | Admin shall soft-delete products (set active=false) |
| FR-ADMIN-04 | Dashboard shall show low-stock alerts (below 5 units) |

### 3.4 Shopping Cart (FR-CART)
| ID | Requirement |
|----|------------|
| FR-CART-01 | Authenticated users shall add products to cart |
| FR-CART-02 | Adding more than available stock shall be rejected |
| FR-CART-03 | Users shall update cart item quantities |
| FR-CART-04 | Users shall remove individual cart items |
| FR-CART-05 | Cart total shall update dynamically |

### 3.5 Order Management (FR-ORDER)
| ID | Requirement |
|----|------------|
| FR-ORDER-01 | Users shall create orders from cart with shipping address |
| FR-ORDER-02 | Stock shall be deducted atomically on order creation |
| FR-ORDER-03 | Order creation shall fail if any item has insufficient stock |
| FR-ORDER-04 | Cart shall be cleared after successful order |
| FR-ORDER-05 | Users shall view their order history and status |
| FR-ORDER-06 | Admin shall update order status (PENDING→CONFIRMED→SHIPPED→DELIVERED) |
| FR-ORDER-07 | Users shall not view other users' orders |

---

## 4. Non-Functional Requirements

| Category | Requirement |
|----------|------------|
| Security | SQL injection prevention via parameterized queries (JPA) |
| Security | CSRF protection for all form submissions |
| Security | XSS prevention via Thymeleaf auto-escaping |
| Performance | Catalogue page should load within 3s (50 products) |
| Reliability | Application should remain available with 99.9% uptime |
| Maintainability | Test coverage ≥ 60% for service layer |
| Scalability | Stateless design — supports horizontal scaling |

---

## 5. Use Cases (Summary)

| UC | Actor | Description |
|----|-------|-------------|
| UC-01 | Guest | Browse and search product catalogue |
| UC-02 | User | Register account |
| UC-03 | User | Login / Logout |
| UC-04 | User | Add product to cart |
| UC-05 | User | Place order |
| UC-06 | User | View order history |
| UC-07 | Admin | Add/Edit/Delete product |
| UC-08 | Admin | Manage order status |
| UC-09 | Admin | Enable/Disable users |
| UC-10 | Admin | View dashboard and low-stock alerts |

---

## 6. Database Design

### ERD Entities
- **User** (id, username, password, email, fullName, enabled, createdAt)
- **Role** (id, name)
- **User_Roles** (user_id, role_id) — Many-to-Many
- **Product** (id, name, description, sku, price, stockQuantity, category, brand, imageUrl, active, createdAt)
- **Order** (id, user_id, status, totalAmount, shippingAddress, createdAt)
- **OrderItem** (id, order_id, product_id, quantity, unitPrice, subtotal)
- **CartItem** (id, user_id, product_id, quantity)

---

## 7. Technology Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| ORM | Spring Data JPA / Hibernate | Prevents SQL injection, reduces boilerplate |
| Auth | Spring Security + BCrypt | Industry standard, proven security |
| Templating | Thymeleaf | Server-side, auto-escapes XSS, Spring integration |
| Database | PostgreSQL | ACID compliance, production-grade |
| CI/CD | Jenkins | Open-source, extensible, Jenkinsfile-as-code |
| Container | Docker | Reproducible deployments, isolation |
| Config Mgmt | Ansible | Idempotent, agentless |
