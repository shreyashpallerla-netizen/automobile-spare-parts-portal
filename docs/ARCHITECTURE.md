# System Architecture — Automobile Spare Parts Portal

## 1. High-Level Architecture Diagram

```mermaid
graph TD
    subgraph Client Tier
        UserBrowser["Web Browser (User / Admin)"]
        UI_Components["Thymeleaf Templates + Bootstrap 5 + JS"]
    end

    subgraph Presentation & Application Tier
        Nginx["Nginx Reverse Proxy / Load Balancer (Port 80)"]
        
        subgraph Spring Boot 3.2 Application (Embedded Tomcat :8080)
            Security["Spring Security (BCrypt, Session Auth, CSRF)"]
            Controllers["MVC Controllers (Auth, Product, Cart, Order, Admin)"]
            Services["Business Services (ProductService, OrderService, etc.)"]
            Repositories["Spring Data JPA Repositories"]
        end
    end

    subgraph Data Tier
        Postgres[("PostgreSQL 16 Database (:5432)\nACID Transactions")]
        H2DB[("H2 In-Memory Database\n(Automated Testing)")]
    end

    UserBrowser -->|HTTPS / HTTP| Nginx
    Nginx -->|Reverse Proxy| Security
    Security --> Controllers
    Controllers --> Services
    Services --> Repositories
    Repositories -->|Production JDBC| Postgres
    Repositories -->|Test Profile JDBC| H2DB
```

---

## 2. Component Architecture

```mermaid
classDiagram
    class User {
        +Long id
        +String username
        +String password
        +String email
        +String fullName
        +boolean enabled
        +Set~Role~ roles
    }
    class Role {
        +Long id
        +RoleName name
    }
    class Product {
        +Long id
        +String name
        +String description
        +String sku
        +BigDecimal price
        +Integer stockQuantity
        +String category
        +String brand
        +boolean active
    }
    class Order {
        +Long id
        +User user
        +OrderStatus status
        +BigDecimal totalAmount
        +String shippingAddress
        +List~OrderItem~ items
    }
    class OrderItem {
        +Long id
        +Order order
        +Product product
        +Integer quantity
        +BigDecimal unitPrice
        +BigDecimal subtotal
    }
    class CartItem {
        +Long id
        +User user
        +Product product
        +Integer quantity
    }

    User "1" --> "*" Role : user_roles
    User "1" --> "*" Order : places
    User "1" --> "*" CartItem : owns
    Order "1" --> "*" OrderItem : contains
    OrderItem "*" --> "1" Product : references
    CartItem "*" --> "1" Product : references
```

---

## 3. Transactional Stock Validation & Order Flow

```mermaid
sequenceDiagram
    autonumber
    actor User as Customer
    participant Cart as CartController & CartService
    participant Order as OrderController & OrderService
    participant DB as ProductRepository & DB

    User->>Cart: POST /cart/add (productId, quantity)
    Cart->>DB: Check stock >= quantity
    alt Sufficient Stock
        Cart->>DB: Save CartItem
        Cart-->>User: Item added to cart
    else Insufficient Stock
        Cart-->>User: StockException ("Insufficient stock")
    end

    User->>Order: POST /orders/place (shippingAddress)
    critical @Transactional Order Creation
        Order->>DB: Fetch user CartItems
        loop For each item in cart
            Order->>DB: Lock & verify Product stock
            alt Stock Available
                Order->>DB: Decrement product stock (stock = stock - qty)
            else Stock Depleted
                Order-->>User: Rollback transaction & throw StockException
            end
        end
        Order->>DB: Save Order & OrderItems
        Order->>DB: Clear user cart
    end
    Order-->>User: Order Confirmed (Order #ID)
```

---

## 4. Deployment Topology

```mermaid
graph LR
    subgraph Host Machine / Cloud VM
        subgraph Docker Bridge Network: autoparts-network
            direction TB
            AppContainer["autoparts-app\n(Spring Boot 3.2)\nPort: 8080"]
            DBContainer["autoparts-postgres\n(PostgreSQL 16)\nPort: 5432"]
        end
        NginxHost["Nginx Host Proxy\nListening :80 -> :8080"]
    end

    ExternalClient["External Client / Browser"] -->|Port 80| NginxHost
    NginxHost --> AppContainer
    AppContainer -->|Internal DNS: autoparts-postgres:5432| DBContainer
```
