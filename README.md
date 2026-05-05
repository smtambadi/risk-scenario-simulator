# Risk Scenario Simulator

Complete project for managing and analyzing risk scenarios with a Spring Boot REST API backend and Python AI service for document analysis.

---

## 📂 Project Structure

```
risk-scenario-simulator/
├── backend/                    # Spring Boot REST API
│   ├── BACKEND_SETUP.md       # ⭐ Backend installation & setup guide
│   ├── API_DOCUMENTATION.md   # Complete API reference
│   ├── src/                   # Java source code
│   └── pom.xml               # Maven configuration
│
├── ai-service/                # Python AI service
│   ├── app.py                # Flask application
│   ├── requirements.txt       # Python dependencies
│   └── routes/               # API endpoints
│
├── README.md                  # This file
└── requirements.txt           # Root dependencies (if any)
```

---

## 🚀 Quick Start

### Backend Setup (Spring Boot + PostgreSQL)

**For detailed instructions, see [backend/BACKEND_SETUP.md](./backend/BACKEND_SETUP.md)**

Quick overview:
```bash
# 1. Prerequisites: Java 17+, Maven 3.6+, PostgreSQL 13+
# 2. Create database
psql -U postgres
CREATE DATABASE risk_simulator;

# 3. Navigate to backend
cd backend

# 4. Configure database (edit src/main/resources/application.properties)
# Update: spring.datasource.password=YOUR_PASSWORD

# 5. Build
mvn clean package -DskipTests

# 6. Run
java -jar target/risk-simulator-0.0.1-SNAPSHOT.jar
```

✅ **API Running on:** http://localhost:8081

### AI Service Setup (Python Flask)

```bash
# 1. Navigate to AI service
cd ai-service

# 2. Install dependencies
pip install -r requirements.txt

# 3. Run
python app.py
```

✅ **Service Running on:** http://localhost:5000

---

## 📖 Documentation

| Component | Documentation |
|-----------|---|
| **Backend REST API** | [backend/BACKEND_SETUP.md](./backend/BACKEND_SETUP.md) |
| **API Endpoints** | [backend/API_DOCUMENTATION.md](./backend/API_DOCUMENTATION.md) |
| **Security** | [SECURITY.md](./SECURITY.md) |

---

## 🔌 Backend REST API

### Available Endpoints

```
POST   /api/risk              → Create risk scenario
GET    /api/risk              → List all scenarios
GET    /api/risk/{id}         → Get by ID
GET    /api/risk/status/{status}     → Filter by status
GET    /api/risk/category/{category} → Filter by category
GET    /api/risk/search?q=term       → Full-text search
PUT    /api/risk/{id}         → Update scenario
DELETE /api/risk/{id}         → Delete scenario
```

### Example Request

```bash
curl -X POST http://localhost:8081/api/risk \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Data Breach Risk",
    "description": "Unauthorized access to customer data",
    "category": "Security",
    "riskLevel": "HIGH",
    "status": "ACTIVE"
  }'
```

For complete API documentation, see [backend/API_DOCUMENTATION.md](./backend/API_DOCUMENTATION.md)

---

## 🛠 Tech Stack

### Backend
- **Spring Boot** 4.0.6 - REST API framework
- **Spring Data JPA** - ORM and data access
- **PostgreSQL** 16.13 - Database
- **Flyway** 10.17.0 - Database migrations
- **Hibernate** 7.2.12 - JPA implementation
- **Maven** - Build tool

### AI Service
- **Flask** - Python web framework
- **Groq API** - LLM integration
- **Chroma** - Vector database for embeddings
- **Redis** - Caching (optional)

---

## 📋 Prerequisites

### For Backend
- ✅ **Java 17+** - Runtime & compilation
- ✅ **Maven 3.6+** - Build tool
- ✅ **PostgreSQL 13+** - Database server
- ✅ **Git** - Version control

### For AI Service
- ✅ **Python 3.8+** - Runtime
- ✅ **pip** - Package manager

---

## 🔧 Configuration

### Backend (application.properties)

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/risk_simulator
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

# Server
server.port=8081

# See backend/BACKEND_SETUP.md for complete configuration
```

### AI Service (.env)

```
GROQ_API_KEY=your_api_key_here
REDIS_URL=redis://localhost:6379
```

---

## ✅ Verification

### Backend Health Check

```bash
# Test API endpoint
curl http://localhost:8081/api/risk

# Expected response: [] (empty array on first run)
```

### Database Verification

```bash
# Connect to PostgreSQL
psql -U postgres -d risk_simulator

# List tables
\dt

# Check schema
\d risk_scenarios
```

---

## 📚 Detailed Setup Guides

### I want to set up the Backend
👉 **[backend/BACKEND_SETUP.md](./backend/BACKEND_SETUP.md)** - Complete step-by-step guide

### I want to understand the API
👉 **[backend/API_DOCUMENTATION.md](./backend/API_DOCUMENTATION.md)** - All endpoints with examples

### I want to deploy to production
👉 **[backend/BACKEND_SETUP.md#security-considerations](./backend/BACKEND_SETUP.md#security-considerations)** - Production checklist

### I'm having issues
👉 **[backend/BACKEND_SETUP.md#troubleshooting](./backend/BACKEND_SETUP.md#troubleshooting)** - Common problems and solutions

---

## 🚦 Common Commands

```bash
# ===== Backend =====
cd backend
mvn clean package -DskipTests        # Build
mvn spring-boot:run                  # Run with Maven
java -jar target/*.jar               # Run JAR

# ===== Database =====
psql -U postgres -h localhost        # Connect to PostgreSQL
CREATE DATABASE risk_simulator;      # Create DB

# ===== Testing =====
curl http://localhost:8081/api/risk  # Test API
```

---

## 📝 License & Security

See [SECURITY.md](./SECURITY.md) for security guidelines and license information.

---

## 🤝 Contributing

1. Clone the repository
2. Create a feature branch
3. Make changes
4. Test thoroughly
5. Submit pull request

---

## 📞 Support

For detailed setup instructions, refer to:
- [Backend Setup Guide](./backend/BACKEND_SETUP.md)
- [API Documentation](./backend/API_DOCUMENTATION.md)
- [Troubleshooting Guide](./backend/BACKEND_SETUP.md#troubleshooting)