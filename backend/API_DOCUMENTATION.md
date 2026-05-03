# Risk Scenario Simulator - REST API Documentation

## Project Summary

Complete Spring Boot backend with:
- **Entity**: `RiskScenario` mapped to `risk_scenarios` table
- **Repository**: `RiskScenarioRepository` with custom queries
- **Service**: `RiskScenarioService` for business logic
- **Controller**: `RiskScenarioController` with REST endpoints
- **Database**: PostgreSQL with Flyway migrations
- **ORM**: Hibernate/JPA with Jakarta Persistence

---

## API Base URL
```
http://localhost:8081/api/risk
```

---

## Endpoints & Postman Examples

### 1. CREATE - POST /api/risk
Create a new risk scenario.

**Request:**
```bash
POST http://localhost:8081/api/risk
Content-Type: application/json

{
  "title": "Data Breach Risk",
  "description": "Risk of unauthorized access to customer data",
  "category": "Security",
  "riskLevel": "HIGH",
  "status": "ACTIVE"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Data Breach Risk",
  "description": "Risk of unauthorized access to customer data",
  "category": "Security",
  "riskLevel": "HIGH",
  "status": "ACTIVE",
  "createdAt": "2026-05-02T22:58:51.123456",
  "updatedAt": "2026-05-02T22:58:51.123456"
}
```

---

### 2. GET ALL - GET /api/risk
Retrieve all risk scenarios.

**Request:**
```bash
GET http://localhost:8081/api/risk
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Data Breach Risk",
    "description": "Risk of unauthorized access to customer data",
    "category": "Security",
    "riskLevel": "HIGH",
    "status": "ACTIVE",
    "createdAt": "2026-05-02T22:58:51.123456",
    "updatedAt": "2026-05-02T22:58:51.123456"
  },
  {
    "id": 2,
    "title": "System Downtime",
    "description": "Unplanned system outages",
    "category": "Operations",
    "riskLevel": "CRITICAL",
    "status": "ACTIVE",
    "createdAt": "2026-05-02T22:59:00.654321",
    "updatedAt": "2026-05-02T22:59:00.654321"
  }
]
```

---

### 3. GET BY ID - GET /api/risk/{id}
Retrieve a specific risk scenario.

**Request:**
```bash
GET http://localhost:8081/api/risk/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Data Breach Risk",
  "description": "Risk of unauthorized access to customer data",
  "category": "Security",
  "riskLevel": "HIGH",
  "status": "ACTIVE",
  "createdAt": "2026-05-02T22:58:51.123456",
  "updatedAt": "2026-05-02T22:58:51.123456"
}
```

---

### 4. GET BY STATUS - GET /api/risk/status/{status}
Filter risk scenarios by status.

**Request:**
```bash
GET http://localhost:8081/api/risk/status/ACTIVE
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Data Breach Risk",
    "description": "Risk of unauthorized access to customer data",
    "category": "Security",
    "riskLevel": "HIGH",
    "status": "ACTIVE",
    "createdAt": "2026-05-02T22:58:51.123456",
    "updatedAt": "2026-05-02T22:58:51.123456"
  }
]
```

---

### 5. GET BY CATEGORY - GET /api/risk/category/{category}
Filter risk scenarios by category.

**Request:**
```bash
GET http://localhost:8081/api/risk/category/Security
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Data Breach Risk",
    "description": "Risk of unauthorized access to customer data",
    "category": "Security",
    "riskLevel": "HIGH",
    "status": "ACTIVE",
    "createdAt": "2026-05-02T22:58:51.123456",
    "updatedAt": "2026-05-02T22:58:51.123456"
  }
]
```

---

### 6. SEARCH - GET /api/risk/search?q={searchTerm}
Search in title and description.

**Request:**
```bash
GET http://localhost:8081/api/risk/search?q=Data
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Data Breach Risk",
    "description": "Risk of unauthorized access to customer data",
    "category": "Security",
    "riskLevel": "HIGH",
    "status": "ACTIVE",
    "createdAt": "2026-05-02T22:58:51.123456",
    "updatedAt": "2026-05-02T22:58:51.123456"
  }
]
```

---

### 7. UPDATE - PUT /api/risk/{id}
Update a risk scenario.

**Request:**
```bash
PUT http://localhost:8081/api/risk/1
Content-Type: application/json

{
  "title": "Critical Data Breach Risk",
  "riskLevel": "CRITICAL",
  "status": "MITIGATED"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Critical Data Breach Risk",
  "description": "Risk of unauthorized access to customer data",
  "category": "Security",
  "riskLevel": "CRITICAL",
  "status": "MITIGATED",
  "createdAt": "2026-05-02T22:58:51.123456",
  "updatedAt": "2026-05-02T23:00:12.987654"
}
```

---

### 8. DELETE - DELETE /api/risk/{id}
Delete a risk scenario.

**Request:**
```bash
DELETE http://localhost:8081/api/risk/1
```

**Response (204 No Content):**
```
(empty response body)
```

---

## Testing in Postman

### Step 1: Import Collection
Create a new Postman Collection called "Risk Scenario API"

### Step 2: Create Requests
Add these requests to your collection:

#### Request 1: Create Risk
- **Method**: POST
- **URL**: `{{base_url}}/api/risk`
- **Headers**: `Content-Type: application/json`
- **Body**:
```json
{
  "title": "Data Breach Risk",
  "description": "Risk of unauthorized access to customer data",
  "category": "Security",
  "riskLevel": "HIGH"
}
```

#### Request 2: Get All
- **Method**: GET
- **URL**: `{{base_url}}/api/risk`

#### Request 3: Get by Status
- **Method**: GET
- **URL**: `{{base_url}}/api/risk/status/ACTIVE`

#### Request 4: Search
- **Method**: GET
- **URL**: `{{base_url}}/api/risk/search?q=Data`

#### Request 5: Update
- **Method**: PUT
- **URL**: `{{base_url}}/api/risk/1`
- **Body**:
```json
{
  "riskLevel": "CRITICAL",
  "status": "MITIGATED"
}
```

#### Request 6: Delete
- **Method**: DELETE
- **URL**: `{{base_url}}/api/risk/1`

### Step 3: Set Environment Variable
In Postman, create an environment variable:
- **Variable**: `base_url`
- **Value**: `http://localhost:8081/api/risk`

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2026-05-02T23:00:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request body"
}
```

### 404 Not Found
```json
{
  "timestamp": "2026-05-02T23:00:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Risk Scenario not found with id: 999"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2026-05-02T23:00:00.000Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## Database Schema

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

CREATE INDEX idx_risk_category ON risk_scenarios(category);
CREATE INDEX idx_risk_status ON risk_scenarios(status);
CREATE INDEX idx_risk_created_at ON risk_scenarios(created_at);
```

---

## File Structure

```
src/main/java/com/risk/risk_simulator/
├── entity/
│   └── RiskScenario.java
├── repository/
│   └── RiskScenarioRepository.java
├── service/
│   └── RiskScenarioService.java
├── controller/
│   └── RiskScenarioController.java
├── config/
│   └── FlywayConfig.java
└── RiskSimulatorApplication.java

src/main/resources/
├── application.properties
└── db/migration/
    ├── V1__init.sql
    └── V2__fix_id_column_type.sql
```

---

## Key Imports

```java
// Entity Annotations
import jakarta.persistence.*;

// Lombok (Getter/Setter/Builder)
import lombok.*;

// Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// Spring Web
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

// Flyway
import org.flywaydb.core.Flyway;
```

---

## Running the Application

1. **Build**:
   ```bash
   mvn clean package -DskipTests
   ```

2. **Run**:
   ```bash
   java -jar target/risk-simulator-0.0.1-SNAPSHOT.jar
   ```

3. **Access API**:
   ```
   http://localhost:8081/api/risk
   ```

---

## Production Checklist

- ✅ Entity mapping with correct data types
- ✅ Repository with custom queries
- ✅ Service layer with business logic
- ✅ REST Controller with proper HTTP methods
- ✅ Error handling and status codes
- ✅ Flyway database migrations
- ✅ CORS enabled for frontend
- ✅ Proper package structure
- ✅ Lombok for reduced boilerplate
- ✅ Constructor injection pattern

---

**API Version**: 1.0.0  
**Last Updated**: May 2, 2026
