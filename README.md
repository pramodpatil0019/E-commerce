# Ecommerce Microservices Platform

An end-to-end ecommerce platform built with **Java 17** and **Spring Boot 3.3.13**, following a microservices architecture with service discovery, API gateway, and JWT authentication.

## Architecture Overview

```
Client → API Gateway (8080) → Eureka Discovery (8761)
                                        ↓
              ┌─────────────────────────────────────────────┐
              │              Microservices                   │
              │                                             │
              │  User Service (8081)      ←→ MySQL user_db  │
              │  Product Service (8082)   ←→ MySQL product_db│
              │  Cart Service (8083)      ←→ MySQL cart_db   │
              │  Order Service (8084)     ←→ MySQL order_db  │
              │  Payment Service (8085)   ←→ MySQL payment_db│
              │  Notification Svc (8086)  ←→ MySQL notif_db  │
              └─────────────────────────────────────────────┘
```

## Services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| Eureka Server | 8761 | — | Service Discovery & Registry |
| API Gateway | 8080 | — | Request routing, JWT validation, CORS |
| User Service | 8081 | user_db | Registration, Login, Profile, JWT Auth |
| Product Service | 8082 | product_db | Product catalog, FULLTEXT search |
| Cart Service | 8083 | cart_db | Shopping cart with Caffeine caching |
| Order Service | 8084 | order_db | Order placement, history, tracking |
| Payment Service | 8085 | payment_db | Payment processing (simulated) |
| Notification Service | 8086 | notification_db | Email notifications (console logs) |

## Tech Stack

- **Java 17** + **Spring Boot 3.3.13** + **Spring Cloud 2023.0.6**
- **MySQL 8** — relational database (all services)
- **Caffeine** — in-memory caching (Cart Service)
- **JWT (jjwt)** — authentication tokens
- **Spring Cloud Gateway** — API gateway
- **Netflix Eureka** — service discovery
- **SpringDoc OpenAPI** — Swagger UI documentation

## Prerequisites

- Java 17 (JDK)
- Maven 3.8+
- MySQL 8.x (running locally)

## Database Setup

Connect to MySQL and run:

```sql
CREATE DATABASE user_db;
CREATE DATABASE product_db;
CREATE DATABASE cart_db;
CREATE DATABASE order_db;
CREATE DATABASE payment_db;
CREATE DATABASE notification_db;
```

> **Note:** Tables are auto-created on first run via `spring.jpa.hibernate.ddl-auto=update`.

## Configuration

Update MySQL credentials in each service's `application.yml` if your local setup differs:

```yaml
spring:
  datasource:
    username: root
    password: root
```

## Build

```bash
mvn clean package -DskipTests
```

## Run (Startup Order)

```bash
# 1. Start Eureka Server first
cd eureka-server && mvn spring-boot:run

# 2. Start API Gateway
cd api-gateway && mvn spring-boot:run

# 3. Start all business services (any order)
cd user-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd cart-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

## API Documentation (Swagger UI)

Once services are running, access Swagger UI for each:

| Service | Swagger UI URL |
|---------|---------------|
| User Service | http://localhost:8081/swagger-ui.html |
| Product Service | http://localhost:8082/swagger-ui.html |
| Cart Service | http://localhost:8083/swagger-ui.html |
| Order Service | http://localhost:8084/swagger-ui.html |
| Payment Service | http://localhost:8085/swagger-ui.html |
| Notification Service | http://localhost:8086/swagger-ui.html |

## Key API Endpoints

### User Service
- `POST /api/users/register` — Register new user
- `POST /api/users/login` — Login (returns JWT)
- `GET /api/users/profile` — Get profile (requires JWT)
- `PUT /api/users/profile` — Update profile (requires JWT)

### Product Service
- `GET /api/products` — List products (paginated)
- `GET /api/products/{id}` — Product details
- `GET /api/products/search?keyword=...` — Full-text search
- `GET /api/products/category/{category}` — Browse by category

### Cart Service
- `GET /api/cart` — Get cart
- `POST /api/cart/items` — Add item
- `PUT /api/cart/items/{productId}` — Update quantity
- `DELETE /api/cart/items/{productId}` — Remove item

### Order Service
- `POST /api/orders` — Place order
- `GET /api/orders` — Order history
- `GET /api/orders/{id}/track` — Track order

### Payment Service
- `POST /api/payments/process` — Process payment
- `GET /api/payments/{id}/receipt` — Get receipt

## Authentication Flow

1. Register via `POST /api/users/register`
2. Login via `POST /api/users/login` → receive JWT token
3. Include token in subsequent requests: `Authorization: Bearer <token>`
4. API Gateway validates JWT and routes to appropriate service

## Git Setup

```bash
git init
git add .
git commit -m "Initial commit: Ecommerce microservices platform"
git remote add origin <your-github-repo-url>
git branch -M main
git push -u origin main
```
