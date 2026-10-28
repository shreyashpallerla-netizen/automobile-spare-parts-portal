# Jenkins CI/CD Pipeline Specification — AutoParts Portal

## 1. Pipeline Flowchart

```mermaid
flowchart TD
    A["Trigger: Git Push or SCM Poll (H/5 * * * *)"] --> B["Stage 1: Checkout SCM"]
    B --> C["Stage 2: Maven Compile"]
    C --> D["Stage 3: Unit & Integration Tests"]
    D --> E["Stage 4: Package JAR & Archive Artifact"]
    E --> F["Stage 5: Docker Build & Tag (v1.0.0-${BUILD_NUMBER})"]
    F --> G["Stage 6: Ephemeral Integration Test"]
    G --> H{"Branch == main?"}
    H -->|Yes| I["Stage 7: Docker Push to Registry"]
    I --> J["Stage 8: Deploy via Docker Compose"]
    J --> K["Stage 9: Health Verification (/actuator/health)"]
    H -->|No (Feature Branch)| L["Skip Deploy & Finish"]
    K --> M["Publish JUnit Reports & Status Alert"]
```

---

## 2. Pipeline Stages & Descriptions

| Stage | Command / Action | Output / Artifact |
|---|---|---|
| **Checkout** | `checkout scm` | Clones target branch and logs commit SHA |
| **Build** | `mvn clean compile -B` | Compiled `.class` files in `target/classes` |
| **Unit Tests** | `mvn test -B` | Runs 20 unit/integration tests; outputs XML reports |
| **Publish Reports** | `junit 'target/surefire-reports/*.xml'` | Test trends & pass/fail charts in Jenkins UI |
| **Package** | `mvn package -DskipTests -B` | Generates `autoparts-portal-1.0.0.jar` |
| **Archive Artifacts** | `archiveArtifacts 'target/*.jar'` | Stores production JAR inside Jenkins build history |
| **Docker Build** | `docker build -t autoparts/autoparts-portal:${TAG} .` | Local Docker image tagged with build ID |
| **Integration Test** | Starts ephemeral Postgres + App containers | Runs curl smoke test against live `/actuator/health` |
| **Docker Push** | `docker.image.push()` (when on `main`) | Pushes versioned image to container registry |
| **Deploy** | `docker-compose up -d autoparts-app` | Zero-downtime container replacement |
| **Health Check** | Retry loop polling `/actuator/health` | Validates HTTP 200 and `"status":"UP"` |

---

## 3. Jenkins Server Configuration Instructions

1. **Global Tool Configuration:**
   - JDK: Add JDK 17 (Name: `JDK-17`)
   - Maven: Add Apache Maven 3.9 (Name: `Maven-3.9`)
2. **Credentials Store:**
   - `docker-registry-credentials`: Username with Password (Docker Hub / Harbor)
   - `postgres-db-credentials`: Database secret credentials
3. **Pipeline Job:**
   - Create new "Pipeline" item.
   - Set Pipeline Definition: "Pipeline script from SCM".
   - SCM: Git → Repository URL → Branch: `*/main`.
   - Script Path: `Jenkinsfile`.
