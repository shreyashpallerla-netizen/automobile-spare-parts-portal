# PROJECT STATUS — Automobile Spare Parts Portal

> **Last Updated:** 2026-10-29 | **Version:** 1.0.0 | **Branch:** main

---

## 📊 Overall Progress

| # | Stage | Status | Notes |
|---|-------|--------|-------|
| 3 | Requirements & Architecture | ✅ DONE | SRS, API docs, DB schema, architecture |
| 4 | Git / GitHub Setup | ✅ DONE | README, .gitignore, commits, branches |
| 5 | Feature Development | ✅ DONE | Full MVP implemented |
| 6 | Jenkins CI | ⏳ READY (Jenkinsfile written; Jenkins install needed) |
| 7 | Jenkins CD | ⏳ READY (Jenkinsfile CD stages written) |
| 8 | Selenium Tests | ✅ DONE (code written; needs Java + app running to execute) |
| 9 | Docker | ⏳ READY (Dockerfile + docker-compose written; Docker install needed) |
| 10 | Jenkins + Docker | ⏳ READY (pipeline stages written) |
| 11 | Ansible | ✅ DONE (playbooks, roles, rollback written) |
| 12 | Reliability | ✅ DONE (idempotent playbook, rollback role, health checks) |

---

## ✅ Stage 3 — Requirements & Architecture

**Status:** COMPLETE

### Deliverables
- [x] Software Requirements Specification → `docs/SRS.md`
- [x] REST API Documentation → `docs/API.md`
- [x] Database Schema (DDL + seed data) → `docs/sql/schema.sql`
- [x] Architecture description in SRS (3-tier: Nginx → Spring Boot → PostgreSQL)
- [x] Local setup instructions → `README.md`

---

## ✅ Stage 4 — Git / GitHub

**Status:** COMPLETE

### Deliverables
- [x] `.gitignore` — covers Java/Maven/IDE/secrets
- [x] `README.md` — badges, tech stack, quick start, Docker, Ansible, structure
- [x] Initial commit: "chore: initial project scaffold"
- [x] Feature commits per module
- [x] Branch strategy: `main` (stable), `develop`, `feature/*`
- [ ] GitHub remote push — *requires GitHub credentials (user action needed)*

---

## ✅ Stage 5 — Feature Development (MVP)

**Status:** COMPLETE — 58 files, full application

### Entities (JPA)
- [x] `User`, `Role`, `Product`, `Order`, `OrderItem`, `CartItem`

### Repositories (Spring Data JPA)
- [x] `UserRepository`, `RoleRepository`, `ProductRepository`
- [x] `OrderRepository`, `CartItemRepository`
- [x] Custom JPQL search queries with `@Query`

### Services
- [x] `ProductService` — CRUD, search, stock adjustment, low-stock alerts
- [x] `CartService` — add/update/remove/clear, stock validation
- [x] `OrderService` — transactional order creation, stock deduction
- [x] `UserService` — register, toggle status, role assignment

### Controllers (MVC)
- [x] `AuthController` — login, register
- [x] `ProductController` — catalogue, detail, search/filter, pagination
- [x] `CartController` — view, add, remove, update quantity
- [x] `OrderController` — checkout, place order, history, detail
- [x] `AdminController` — dashboard, product/user/order management
- [x] `HomeController` — redirect, 403 error page

### Security
- [x] BCrypt password encoding
- [x] Session-based authentication
- [x] RBAC: `ROLE_ADMIN`, `ROLE_USER`
- [x] CSRF protection on all forms
- [x] `CustomUserDetailsService`
- [x] Data initializer seeds admin + demo user on startup

### Validation
- [x] Bean Validation (`@NotBlank`, `@Email`, `@Min`, `@Pattern`, etc.)
- [x] Server-side form error display in Thymeleaf
- [x] `GlobalExceptionHandler` for all exception types

### Frontend
- [x] Bootstrap 5 responsive design
- [x] Product catalogue grid with search, category filter, sort, pagination
- [x] Admin sidebar layout with dashboard, product CRUD, orders, users
- [x] Flash message alerts (auto-dismiss via JS)
- [x] Low-stock badge indicators

---

## ⏳ Stage 6 — Jenkins CI

**Status:** READY (Jenkinsfile written, Jenkins not yet installed)

### Deliverables
- [x] `Jenkinsfile` — pipeline with Checkout, Build, Test, Package, Docker, Deploy, Health Check stages
- [x] SCM polling trigger (`H/5 * * * *`)
- [x] Artifact archiving (`target/*.jar`)
- [x] JUnit test report publishing
- [ ] Jenkins installation — **PREREQUISITE NEEDED**
- [ ] Jenkins pipeline job creation — requires Jenkins

---

## ⏳ Stage 7 — Jenkins CD

**Status:** READY (Pipeline stages written in Jenkinsfile)

### Jenkinsfile CD Stages
- [x] Docker Build stage
- [x] Docker Push stage (when `branch == main`)
- [x] Deploy stage (docker-compose)
- [x] Health check stage (retry loop on `/actuator/health`)
- [ ] Requires Jenkins + Docker running

---

## ✅ Stage 8 — Selenium Tests

**Status:** CODE COMPLETE (execution needs Java + running app)

### Tests Written
| Test | ID | Description |
|------|----|-------------|
| Login with valid credentials | TC-01 | Admin login → redirect to /products |
| Product search | TC-02 | Search input returns results |
| Add to cart | TC-03 | User adds product to cart |
| Admin product form validation | TC-04 | Controlled failure → validation errors → fix |
| Logout | TC-05 | Session cleared, redirect to login |

- [x] ChromeDriver auto-managed via WebDriverManager
- [x] Headless Chrome execution
- [x] Screenshots captured after every test → `target/screenshots/`
- [x] Controlled failure demonstrated in TC-04
- [ ] Execution pending (needs Java 17 + Maven + app running)

---

## ⏳ Stage 9 — Docker

**Status:** READY (files written; Docker not installed)

### Deliverables
- [x] `Dockerfile` — multi-stage build (Maven builder + JRE runtime)
- [x] `docker-compose.yml` — app + postgres with health checks
- [x] Non-root user `autoparts` in container
- [x] Environment variable injection (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`)
- [x] `HEALTHCHECK` instruction in Dockerfile
- [ ] `docker build` execution — requires Docker Desktop
- [ ] Container run + log inspection — requires Docker

---

## ⏳ Stage 10 — Jenkins + Docker Integration

**Status:** READY (Jenkinsfile includes Docker stages)

- [x] `Docker Build` stage in Jenkinsfile
- [x] Integration test using ephemeral Docker containers in CI
- [x] `Docker Push` on `main` branch
- [x] Deployment via `docker-compose up`
- [ ] Requires Jenkins + Docker

---

## ✅ Stage 11 — Ansible

**Status:** COMPLETE

### Deliverables
- [x] `ansible/inventory.ini` — local + remote targets
- [x] `ansible/playbook.yml` — deploy app with health check post-task
- [x] `ansible/setup-environment.yml` — install Docker, create dirs, firewall
- [x] `ansible/roles/autoparts/tasks/main.yml` — PostgreSQL + App container deployment
- [x] `ansible/roles/autoparts/tasks/rollback.yml` — rollback to `previous-stable` image
- [x] `ansible/roles/autoparts/templates/nginx.conf.j2` — Nginx reverse proxy
- [x] `ansible/roles/autoparts/vars/main.yml` — role variables

---

## ✅ Stage 12 — Reliability

**Status:** COMPLETE (documented and coded)

- [x] **Idempotency** — All Ansible tasks use `state: present/started/absent` (safe to re-run)
- [x] **Health Checks** — `uri` module retries 10x with 10s delay
- [x] **Rollback** — `rollback.yml` restarts `previous-stable` Docker image
- [x] **Docker HEALTHCHECK** — in Dockerfile + docker-compose
- [x] **Transactional stock** — `@Transactional` on `OrderServiceImpl.createOrder()`

---

## 🚨 Prerequisites Needed (User Action Required)

| Tool | Required For | Download |
|------|-------------|----------|
| **Java JDK 17** | Build, Run, Tests | https://adoptium.net/temurin/releases/?version=17 |
| **Maven 3.9** | Build, Tests | https://maven.apache.org/download.cgi |
| **Docker Desktop** | Stages 9–10 | https://www.docker.com/products/docker-desktop/ |
| **Jenkins** | Stages 6–7, 10 | https://www.jenkins.io/download/ |

> After installing Java + Maven, run: `mvn test` to execute all unit + integration tests.

---

## 📁 Project Location
**`C:\Users\admin\AppData\Local\Temp\autoparts\`**

## 🔑 Default Credentials
| User | Password | Role |
|------|----------|------|
| `admin` | `Admin@123` | ADMIN + USER |
| `user1` | `User@123` | USER |
