# Risk Scenario Simulator - Backend Setup Guide

Complete installation and configuration guide for the Risk Scenario Simulator Spring Boot backend.

---

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Project Overview](#project-overview)
3. [Database Setup](#database-setup)
4. [Project Structure](#project-structure)
5. [Build & Installation](#build--installation)
6. [Running the Application](#running-the-application)
7. [API Testing](#api-testing)
8. [Database Schema](#database-schema)
9. [Configuration](#configuration)
10. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

Before starting, ensure you have the following installed:

| Software | Version | Purpose |
|----------|---------|---------|
| **Java JDK** | 17 or higher | Runtime & compilation |
| **Maven** | 3.6.0 or higher | Build tool & dependency management |
| **PostgreSQL** | 13 or higher | Database server |
| **Git** | 2.0+ | Version control (for cloning) |

### Verify Installation

```bash
# Check Java
java -version
# Output should show Java 17+

# Check Maven  
mvn -version
# Output should show Maven 3.6.0+

# Check PostgreSQL
psql --version
# Output should show PostgreSQL 13+
```

---

## Project Overview

**Risk Scenario Simulator Backend** is a Spring Boot 4.0.6 REST API that manages risk scenarios with the following features:

- ✅ **Full CRUD Operations** - Create, read, update, delete risk scenarios
- ✅ **Advanced Querying** - Filter by status, category, date range, and full-text search
- ✅ **Database Auditing** - Automatic timestamp tracking with Spring Data Auditing
- ✅ **Error Handling** - Custom exception hierarchy with meaningful error messages
- ✅ **Database Migrations** - Flyway-managed schema evolution
- ✅ **Security** - Spring Security configured for REST API access

### Key Technologies

| Component | Version | Role |
|-----------|---------|------|
| Spring Boot | 4.0.6 | Application framework |
| Spring Data JPA | Latest | ORM & data access layer |
| Hibernate | 7.2.12 | JPA implementation |
| PostgreSQL Driver | 42.7.10 | Database connectivity |
| Flyway | 10.17.0 | Database migrations |
| Lombok | 1.18.x | Code generation |
| Apache Tomcat | 11.0.21 | Web server (embedded) |

---

## Database Setup

### Step 1: Start PostgreSQL Service

**Windows:**
```bash
# If installed as service, verify it's running
sc query PostgreSQL
# Output should show "RUNNING"

# If not running, start it:
net start PostgreSQL
```

**Linux/Mac:**
```bash
# Start PostgreSQL service
sudo systemctl start postgresql

# Verify it's running
sudo systemctl status postgresql
```

### Step 2: Connect to PostgreSQL

```bash
# Connect using default postgres user
psql -U postgres -h localhost

# Enter the default password when prompted
# (depends on your PostgreSQL installation)
```

### Step 3: Create Application Database

```sql
-- Inside psql prompt:
CREATE DATABASE risk_simulator;

-- Verify creation
\l
-- You should see "risk_simulator" in the list

-- Exit psql
\q
```

### Step 4: Verify Database Connection

```bash
# Test connection to the new database
psql -U postgres -h localhost -d risk_simulator -c "SELECT 1;"
# Output: "1" means connection successful
```

> **Note:** Default credentials are:
> - **Host:** localhost
> - **Port:** 5432
> - **User:** postgres
> - **Password:** (set during PostgreSQL installation)
> - **Database:** risk_simulator

---

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/risk/risk_simulator/
│   │   │   ├── RiskSimulatorApplication.java       # Main entry point
│   │   │   ├── config/
│   │   │   │   ├── AuditingConfig.java            # Enable Spring Data Auditing
│   │   │   │   ├── FlywayConfig.java              # Flyway configuration
│   │   │   │   └── SecurityConfig.java            # Spring Security setup
│   │   │   ├── controller/
│   │   │   │   └── RiskScenarioController.java    # REST endpoints
│   │   │   ├── entity/
│   │   │   │   └── RiskScenario.java              # JPA entity
│   │   │   ├── exception/
│   │   │   │   ├── RiskScenarioException.java     # Base exception
│   │   │   │   ├── ResourceNotFoundException.java # 404 exception
│   │   │   │   └── InvalidRiskScenarioException.java # Validation exception
│   │   │   ├── repository/
│   │   │   │   └── RiskScenarioRepository.java    # Data access layer
│   │   │   └── service/
│   │   │       └── RiskScenarioService.java       # Business logic
│   │   └── resources/
│   │       ├── application.properties             # Configuration
│   │       └── db/migration/
│   │           ├── V1__init.sql                   # Create tables
│   │           └── V2__create_audit_log.sql       # Audit log table
│   └── test/
│       └── java/.../RiskSimulatorApplicationTests.java
├── pom.xml                                         # Maven configuration
├── API_DOCUMENTATION.md                            # API reference
├── BACKEND_SETUP.md                                # This file
└── mvnw / mvnw.cmd                                # Maven wrapper
```

### Key Files Explained

| File | Purpose |
|------|---------|
| `RiskScenarioApplication.java` | Spring Boot application entry point |
| `RiskScenarioController.java` | Exposes 8 REST endpoints |
| `RiskScenarioService.java` | Business logic with validation |
| `RiskScenarioRepository.java` | JPA queries and data access |
| `RiskScenario.java` | JPA entity with Spring Data Auditing |
| `application.properties` | Database & Flyway configuration |
| `V1__init.sql` | Creates risk_scenarios table |
| `V2__create_audit_log.sql` | Creates audit_log table |

---

## Build & Installation

### Step 1: Clone or Navigate to Project

```bash
# Clone if you haven't already
git clone <repository-url>
cd risk-scenario-simulator/backend

# Or navigate if already cloned
cd d:\Projects\risk-scenario-simulator\backend  # Windows
cd ~/Projects/risk-scenario-simulator/backend    # Linux/Mac
```

### Step 2: Verify Maven Installation

```bash
# Using system Maven (if installed globally)
mvn -v

# Or use the included Maven wrapper
./mvnw -v              # Linux/Mac
mvnw.cmd -v            # Windows
```

### Step 3: Update application.properties

Edit `src/main/resources/application.properties` and update database credentials if needed:

```properties
# Database connection
spring.datasource.url=jdbc:postgresql://localhost:5432/risk_simulator
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE
spring.datasource.driver-class-name=org.postgresql.Driver
```

Replace `YOUR_PASSWORD_HERE` with your PostgreSQL postgres user password.

### Step 4: Build the Project

```bash
# Clean previous builds and compile (skip tests)
mvn clean package -DskipTests

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: ~15 seconds
```

**If build fails:**
- Ensure Java 17+ is installed: `java -version`
- Ensure Maven 3.6.0+ is installed: `mvn -version`
- Check network connectivity (Maven downloads dependencies)
- See [Troubleshooting](#troubleshooting) section

### Step 5: Verify Build Artifact

```bash
# Check if JAR was created
ls target/risk-simulator-0.0.1-SNAPSHOT.jar

# On Windows:
dir target\risk-simulator-0.0.1-SNAPSHOT.jar
```

---

## Running the Application

### Start the Application

**Option 1: Using Maven (Recommended for Development)**

```bash
# Navigate to backend directory
cd backend

# Run directly with Maven
mvn spring-boot:run

# Expected output will show:
# ... Spring Boot :: (v4.0.6)
# ... Tomcat initialized with port 8081
# ... Application started successfully
```

**Option 2: Using Java JAR (Production)**

```bash
# Navigate to backend directory
cd backend

# Run the built JAR
java -jar target/risk-simulator-0.0.1-SNAPSHOT.jar

# Expected output:
# ... Spring Boot :: (v4.0.6)
# ... Tomcat initialized with port 8081
# ... Application started successfully
```

### Verify Application Started

```bash
# In a new terminal, check if server is responding
curl http://localhost:8081/api/risk

# Windows PowerShell alternative:
Invoke-WebRequest -Uri "http://localhost:8081/api/risk"

# Expected response: [] (empty array, API is running)
```

### Application Startup Process

When the application starts, the following happens automatically:

```
1. Spring Boot initializes
   └─> Loads application.properties

2. Flyway Database Migrations
   └─> Checks migration history in database
   └─> Executes V1__init.sql (create risk_scenarios table)
   └─> Executes V2__create_audit_log.sql (create audit_log table)
   └─> Updates schema version to 2

3. Hibernate JPA
   └─> Validates entity mappings against database schema
   └─> Creates EntityManagerFactory
   └─> Initializes Spring Data repositories

4. Spring Data Auditing
   └─> Activates @CreatedDate/@LastModifiedDate interceptors

5. Tomcat Web Server
   └─> Starts on port 8081 (http://localhost:8081)

6. Spring Security
   └─> Permits /api/** requests without authentication
```

---

## API Testing

### Quick Test with curl

**Get all risk scenarios:**
```bash
curl http://localhost:8081/api/risk
```

**Create a new risk scenario:**
```bash
curl -X POST http://localhost:8081/api/risk \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Data Breach Risk",
    "description": "Unauthorized access to sensitive customer data",
    "category": "Security",
    "riskLevel": "HIGH",
    "status": "ACTIVE"
  }'
```

### Testing with Postman

1. **Download Postman** from https://www.postman.com/downloads/
2. **Import Collection** (if available) or create requests manually:

| Method | Endpoint | Body (JSON) | Expected Status |
|--------|----------|-------------|-----------------|
| POST | `http://localhost:8081/api/risk` | See below | 201 |
| GET | `http://localhost:8081/api/risk` | N/A | 200 |
| GET | `http://localhost:8081/api/risk/1` | N/A | 200 |
| GET | `http://localhost:8081/api/risk/status/ACTIVE` | N/A | 200 |
| GET | `http://localhost:8081/api/risk/category/Security` | N/A | 200 |
| GET | `http://localhost:8081/api/risk/search?q=breach` | N/A | 200 |
| PUT | `http://localhost:8081/api/risk/1` | See below | 200 |
| DELETE | `http://localhost:8081/api/risk/1` | N/A | 204 |

**Sample POST Body (Create Risk Scenario):**
```json
{
  "title": "Data Breach Risk",
  "description": "Unauthorized access to sensitive customer data",
  "category": "Security",
  "riskLevel": "HIGH",
  "status": "ACTIVE"
}
```

**Sample PUT Body (Update Risk Scenario):**
```json
{
  "title": "Data Breach Risk - Updated",
  "description": "Updated description",
  "category": "Information Security",
  "riskLevel": "CRITICAL",
  "status": "MITIGATED"
}
```

For complete API documentation, see [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

---

## Database Schema

### risk_scenarios Table

Primary table for storing risk scenarios:

```sql
CREATE TABLE risk_scenarios (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100),
    risk_level VARCHAR(50),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for query optimization
CREATE INDEX idx_risk_category ON risk_scenarios(category);
CREATE INDEX idx_risk_status ON risk_scenarios(status);
CREATE INDEX idx_risk_created_at ON risk_scenarios(created_at);
```

### audit_log Table

Tracks changes to risk_scenarios for audit trails:

```sql
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id INTEGER NOT NULL,
    action VARCHAR(20) NOT NULL,  -- INSERT, UPDATE, DELETE
    changed_fields TEXT,
    old_values TEXT,
    new_values TEXT,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Multiple indexes for different query patterns
CREATE INDEX idx_audit_entity_type_id ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_action ON audit_log(action);
CREATE INDEX idx_audit_changed_at ON audit_log(changed_at);
CREATE INDEX idx_audit_entity_type ON audit_log(entity_type);
CREATE INDEX idx_audit_entity_id ON audit_log(entity_id);
CREATE INDEX idx_audit_entity_date ON audit_log(entity_type, entity_id, changed_at);
```

### View Schema in Database

```bash
# Connect to database
psql -U postgres -h localhost -d risk_simulator

# List all tables
\dt

# View risk_scenarios schema
\d risk_scenarios

# View audit_log schema
\d audit_log

# View indexes
\di

# Exit
\q
```

---

## Configuration

### application.properties

Located at `src/main/resources/application.properties`

```properties
# ===== Spring Boot Configuration =====
spring.application.name=risk-simulator

# ===== PostgreSQL Database =====
spring.datasource.url=jdbc:postgresql://localhost:5432/risk_simulator
spring.datasource.username=postgres
spring.datasource.password=1253236
spring.datasource.driver-class-name=org.postgresql.Driver

# ===== JPA / Hibernate Configuration =====
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# ===== Flyway Database Migration =====
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0

# ===== Server Configuration =====
server.port=8081
server.servlet.context-path=/
```

### Environment-Specific Configuration

**Development (application.properties):**
```properties
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
logging.level.root=INFO
```

**Production (application-prod.properties):**
```properties
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
logging.level.root=WARN
server.port=8080
```

To use production profile:
```bash
java -jar target/risk-simulator-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## Troubleshooting

### Issue: Database Connection Failed

**Error:** `java.sql.SQLException: Connection to localhost:5432 refused`

**Solutions:**
1. Check PostgreSQL is running:
   ```bash
   psql -U postgres -h localhost -c "SELECT 1;"
   ```

2. Verify database exists:
   ```bash
   psql -U postgres -h localhost -l | grep risk_simulator
   ```

3. Check credentials in `application.properties`:
   - Database URL: `jdbc:postgresql://localhost:5432/risk_simulator`
   - Username: `postgres`
   - Password: matches your PostgreSQL installation

### Issue: Maven Build Fails

**Error:** `[ERROR] BUILD FAILURE`

**Solutions:**
1. Verify Java 17+:
   ```bash
   java -version
   ```

2. Verify Maven 3.6.0+:
   ```bash
   mvn -version
   ```

3. Clear Maven cache and rebuild:
   ```bash
   mvn clean package -DskipTests
   ```

4. Check internet connectivity (Maven downloads dependencies)

### Issue: Port 8081 Already in Use

**Error:** `Tomcat port 8081 already in use`

**Solutions:**
1. Find what's using port 8081:
   ```bash
   # Windows
   netstat -ano | findstr :8081
   
   # Linux/Mac
   lsof -i :8081
   ```

2. Kill the process:
   ```bash
   # Windows
   taskkill /PID <PID> /F
   
   # Linux/Mac
   kill -9 <PID>
   ```

3. Or change port in `application.properties`:
   ```properties
   server.port=8082
   ```

### Issue: Flyway Migration Error

**Error:** `Migration checksum mismatch for migration version 1`

**Solutions:**
1. Check migration files haven't been modified:
   ```bash
   cat src/main/resources/db/migration/V1__init.sql
   ```

2. If modified, reset the migration history:
   ```bash
   psql -U postgres -d risk_simulator
   DELETE FROM flyway_schema_history WHERE version = 1;
   \q
   ```

3. Rebuild and restart:
   ```bash
   mvn clean package -DskipTests
   java -jar target/risk-simulator-0.0.1-SNAPSHOT.jar
   ```

### Issue: Hibernate Validation Error

**Error:** `HibernateException: Column type mismatch for column [id]`

**Solutions:**
1. Verify entity mapping matches database schema
2. Ensure migrations have been executed
3. Check database schema:
   ```bash
   psql -U postgres -d risk_simulator
   \d risk_scenarios
   ```

### Issue: Spring Security Blocking API

**Error:** `HTTP 401 Unauthorized`

**Solutions:**
1. Verify `SecurityConfig.java` permits `/api/**`:
   ```java
   .authorizeHttpRequests(authz -> authz.requestMatchers("/api/**").permitAll()...
   ```

2. Ensure CSRF is disabled for REST API:
   ```java
   .csrf(csrf -> csrf.disable())
   ```

### Checking Application Logs

Application logs show startup process and help diagnose issues:

```bash
# Tail logs in real-time (if running in background)
tail -f nohup.out

# Save logs to file for analysis
java -jar target/risk-simulator-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

---

## Development Workflow

### Making Code Changes

1. **Edit Java source files** in `src/main/java/com/risk/risk_simulator/`
2. **Rebuild** with Maven:
   ```bash
   mvn clean package -DskipTests
   ```
3. **Stop** running application (Ctrl+C)
4. **Restart** application:
   ```bash
   java -jar target/risk-simulator-0.0.1-SNAPSHOT.jar
   ```

### Adding New Endpoints

1. Create controller method in `RiskScenarioController.java`
2. Add service logic in `RiskScenarioService.java` if needed
3. Add repository query in `RiskScenarioRepository.java` if needed
4. Rebuild and test

### Adding Database Migrations

1. Create new migration file: `src/main/resources/db/migration/V3__add_new_table.sql`
2. Naming convention: `V{VERSION}__{DESCRIPTION}.sql`
3. Flyway will execute automatically on next startup

---

## Performance Tips

1. **Use prepared statements** - JPA handles this automatically
2. **Add indexes** - See database schema for existing indexes
3. **Limit query results** - Use pagination for large datasets
4. **Cache frequently accessed data** - Spring Cache support
5. **Monitor slow queries** - Enable in application.properties:
   ```properties
   spring.jpa.properties.hibernate.generate_statistics=true
   logging.level.org.hibernate.stat=DEBUG
   ```

---

## Security Considerations

### Before Production Deployment

- ✅ Change default PostgreSQL password
- ✅ Use environment variables for sensitive credentials:
  ```bash
  export DB_PASSWORD=secure_password
  export DB_USER=prod_user
  ```
- ✅ Enable HTTPS/SSL
- ✅ Configure CORS appropriately
- ✅ Implement authentication/authorization
- ✅ Add rate limiting
- ✅ Review Spring Security configuration
- ✅ Use parameterized queries (JPA does this)

### Credential Management

**Never commit sensitive credentials!** Use environment variables:

```bash
# Set environment variables
export SPRING_DATASOURCE_PASSWORD=secure_password
export SPRING_DATASOURCE_USERNAME=prod_user

# Run application
java -jar target/risk-simulator-0.0.1-SNAPSHOT.jar
```

---

## Useful Commands Reference

```bash
# ===== Build & Run =====
mvn clean package -DskipTests        # Build without tests
mvn spring-boot:run                  # Run with Maven
java -jar target/*.jar               # Run built JAR

# ===== Database =====
psql -U postgres -h localhost        # Connect to PostgreSQL
psql -U postgres -d risk_simulator -c "SELECT * FROM risk_scenarios;" # Query

# ===== Testing =====
curl http://localhost:8081/api/risk  # Get all scenarios
curl -X POST http://localhost:8081/api/risk -H "Content-Type: application/json" -d '{...}'

# ===== Process Management =====
netstat -ano | findstr :8081         # Find process on port (Windows)
tasklist | findstr java              # List Java processes
taskkill /PID <PID> /F               # Kill process (Windows)

# ===== Logs =====
tail -f app.log                      # Stream logs (Linux/Mac)
Get-Content app.log -Tail 20         # Last 20 lines (Windows)
```

---

## Next Steps

After successful setup:

1. ✅ **Test All Endpoints** - See [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
2. ✅ **Create Sample Data** - Use POST /api/risk endpoint
3. ✅ **Verify Database** - Query risk_scenarios table
4. ✅ **Check Logs** - Monitor application logs
5. ✅ **Customize Configuration** - Update application.properties as needed
6. ✅ **Deploy** - Follow production deployment guidelines

---

## Getting Help

- 📖 [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- 📖 [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- 📖 [Flyway Documentation](https://flywaydb.org/documentation/)
- 📖 [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- 📄 [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - Complete API reference

---

## Summary Checklist

- [ ] Java 17+ installed and verified
- [ ] Maven 3.6.0+ installed and verified
- [ ] PostgreSQL 13+ running
- [ ] risk_simulator database created
- [ ] application.properties configured with correct credentials
- [ ] Project built successfully: `mvn clean package -DskipTests`
- [ ] Application started: `java -jar target/*.jar`
- [ ] API tested: `curl http://localhost:8081/api/risk`
- [ ] Sample data created via POST endpoint
- [ ] Logs monitored for errors

**You're ready to go! 🚀**
