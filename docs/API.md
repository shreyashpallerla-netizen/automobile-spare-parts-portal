# REST API Documentation
## AutoParts Portal v1.0

> Note: The primary interface is Thymeleaf MVC (HTML/form-based). These REST-style endpoints are implemented via HTTP POST/GET forms through Spring MVC.

---

## Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/auth/login` | Display login form |
| POST | `/auth/login` | Authenticate user (Spring Security) |
| GET | `/auth/register` | Display registration form |
| POST | `/auth/register` | Register new user |
| GET | `/auth/logout` | Logout and invalidate session |

---

## Product Endpoints (Public)

| Method | Endpoint | Description | Query Params |
|--------|----------|-------------|-------------|
| GET | `/products` | Product catalogue | `search`, `category`, `page`, `size`, `sort` |
| GET | `/products/{id}` | Product detail | — |

---

## Cart Endpoints (Authenticated Users)

| Method | Endpoint | Description | Body Params |
|--------|----------|-------------|------------|
| GET | `/cart` | View shopping cart | — |
| POST | `/cart/add` | Add item to cart | `productId`, `quantity` |
| POST | `/cart/remove/{id}` | Remove cart item | — |
| POST | `/cart/update/{id}` | Update item quantity | `quantity` |

---

## Order Endpoints (Authenticated Users)

| Method | Endpoint | Description | Body Params |
|--------|----------|-------------|------------|
| GET | `/orders` | User's order history | — |
| GET | `/orders/checkout` | Checkout form | — |
| POST | `/orders/place` | Place order | `shippingAddress` |
| GET | `/orders/{id}` | Order detail | — |

---

## Admin Endpoints (ROLE_ADMIN only)

### Dashboard
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin` | Admin dashboard |

### Product Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/products` | List all products |
| GET | `/admin/products/new` | New product form |
| POST | `/admin/products/new` | Create product |
| GET | `/admin/products/{id}/edit` | Edit product form |
| POST | `/admin/products/{id}/edit` | Update product |
| POST | `/admin/products/{id}/delete` | Deactivate product |

### User Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/users` | List all users |
| POST | `/admin/users/{id}/toggle` | Enable/disable user |

### Order Management
| Method | Endpoint | Description | Body Params |
|--------|----------|-------------|------------|
| GET | `/admin/orders` | List all orders | — |
| POST | `/admin/orders/{id}/status` | Update order status | `status` |

---

## Validation Rules

### Product
- `name`: 2–200 characters, required
- `sku`: 3–20 uppercase alphanumeric + hyphens, unique
- `price`: > 0.01, ≤ 999999.99
- `stockQuantity`: ≥ 0
- `category`: required

### User Registration
- `username`: 3–50 chars, alphanumeric + underscore only
- `password`: ≥6 chars, must contain uppercase, lowercase, digit, special char
- `email`: valid email format, unique
- `fullName`: 2–100 characters

### Order
- `shippingAddress`: 10–500 characters

---

## Error Responses

| HTTP Status | Scenario |
|-------------|----------|
| 400 | Validation error, stock insufficient, duplicate SKU |
| 401 | Not authenticated |
| 403 | Insufficient role permissions |
| 404 | Product/Order/User not found |
| 500 | Unexpected server error |

All errors render the `error/error.html` template with descriptive messages.
