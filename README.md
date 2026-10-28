# Automobile Spare Parts Portal

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](./Jenkinsfile)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)](./Dockerfile)

A complete, production-ready **Automobile Spare Parts e-commerce portal** with an end-to-end DevOps pipeline.

---

## 🚀 Features

| Feature | Status |
|---------|--------|
| 🔐 Secure Login / Registration | ✅ |
| 🗂️ Product Catalogue with Search & Filter | ✅ |
| ➕ Add / Edit / Deactivate Products (Admin) | ✅ |
| 📦 Stock Management with Transactional Validation | ✅ |
| 🛒 Shopping Cart | ✅ |
| 📋 Order Creation & History | ✅ |
| 🔄 Order Status Management (Admin) | ✅ |
| 👥 User Management (Admin) | ✅ |
| ✅ Input Validation & Exception Alerts | ✅ |
| 🏷️ Role-Based Access (ADMIN / USER) | ✅ |

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf, Bootstrap 5, HTML/CSS/JavaScript
- **Database:** PostgreSQL 16 (H2 for tests)
- **Build:** Maven 3.9
- **CI/CD:** Jenkins
- **Testing:** JUnit 5, Mockito, Selenium WebDriver
- **Containerization:** Docker, Docker Compose
- **Configuration Management:** Ansible
- **Reverse Proxy:** Nginx
- **VCS:** Git / GitHub

---

## 📋 Prerequisites

| Tool | Version | Installation |
|------|---------|-------------|
| Java JDK | 17+ | [Download](https://adoptium.net/) |
| Maven | 3.9+ | [Download](https://maven.apache.org/) |
| PostgreSQL | 15+ | [Download](https://www.postgresql.org/) |
| Git | 2.x | [Download](https://git-scm.com/) |
| Docker | 24+ | [Download](https://www.docker.com/) |
| Docker Compose | 2.x | Included with Docker Desktop |

---

## ⚡ Quick Start (Local)

### 1. Clone the repository
```bash
git clone https://github.com/<your-org>/autoparts-portal.git
cd autoparts-portal
```

### 2. Set up PostgreSQL
```sql
CREATE DATABASE autoparts;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE autoparts TO postgres;
```

### 3. Configure environment variables (optional)
```bash
export DB_URL=jdbc:postgresql://localhost:5432/autoparts
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

### 4. Run the application
```bash
mvn spring-boot:run
```

### 5. Access the portal
Open [http://localhost:8080](http://localhost:8080)

**Default credentials:**
- Admin: `admin` / `Admin@123`
- User: `user1` / `User@123`

---

## 🐳 Docker Deployment

```bash
# Using Docker Compose (recommended)
docker-compose up -d

# Or build and run manually
docker build -t autoparts/autoparts-portal:1.0.0 .
docker run -d -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://localhost:5432/autoparts \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  autoparts/autoparts-portal:1.0.0
```

---

## 🧪 Running Tests

```bash
# Unit tests (fast, no database required)
mvn test

# Integration tests (requires H2 in-memory DB - auto configured)
mvn verify

# Selenium UI tests (requires running application)
mvn test -Pselselenium -Dapp.url=http://localhost:8080
```

---

## 🔧 Jenkins CI/CD

1. Configure Jenkins with JDK 17 and Maven 3.9 tools
2. Create a pipeline job pointing to this repository
3. Set credentials: `docker-registry-credentials`, `postgres-db-credentials`
4. Build triggers: SCM polling every 5 minutes

---

## 📖 Ansible Deployment

```bash
# Setup environment
ansible-playbook -i ansible/inventory.ini ansible/setup-environment.yml

# Deploy application
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml

# Rollback to previous stable
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --tags rollback
```

---

## 📁 Project Structure

```
autoparts-portal/
├── src/
│   ├── main/
│   │   ├── java/com/autoparts/
│   │   │   ├── config/          # Security & data init config
│   │   │   ├── controller/      # MVC controllers
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Custom exceptions & handler
│   │   │   ├── repository/      # Spring Data JPA repos
│   │   │   ├── security/        # UserDetailsService
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── static/          # CSS, JS
│   │       └── templates/       # Thymeleaf HTML templates
│   └── test/
│       └── java/com/autoparts/
│           ├── unit/            # Mockito unit tests
│           ├── integration/     # JPA integration tests
│           └── selenium/        # Selenium UI tests
├── ansible/                     # Ansible playbooks & roles
├── docs/                        # SRS, diagrams, SQL schema
├── Dockerfile                   # Multi-stage Docker build
├── docker-compose.yml           # Multi-container setup
├── Jenkinsfile                  # CI/CD pipeline definition
└── pom.xml                      # Maven build config
```

---

## 📚 Documentation

- [Software Requirements Specification](docs/SRS.md)
- [API Documentation](docs/API.md)
- [Database Schema](docs/sql/schema.sql)
- [Architecture Diagram](docs/ARCHITECTURE.md)

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -m "feat: add your feature"`
4. Push and create a Pull Request

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.
