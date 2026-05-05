# Risk Scenario Simulator - Backend End-to-End Testing Guide

Complete guide for testing the Spring Boot REST API backend using Postman.

---

## 📋 Prerequisites

1. **Java 17+** installed
2. **Maven 3.6+** installed
3. **PostgreSQL 13+** running
4. **Postman** installed ([Download](https://www.postman.com/downloads/))
5. Backend running on `http://localhost:8081`

---

## 🚀 Step 1: Start the Backend

```bash
# Navigate to backend directory
cd backend

# Build the project
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run
```

Expected output:
```
Started RiskSimulatorApplication in X seconds
```

Verify: Visit `http://localhost:8081/api/risk` - should return a JSON array (empty or with existing data)

---

## 📊 Step 2: Import Postman Collection

### Option A: Import JSON File
1. Open Postman
2. Click **Import** button (top-left)
3. Select **Upload Files**
4. Choose `Postman_Collection.json` from project root
5. Click **Import**

### Option B: Manual Setup
If you prefer to create requests manually, follow the endpoint documentation below.

---

## 🔧 Step 3: Configure Environment Variables

In Postman, set these collection variables:

| Variable | Value | Description |
|----------|-------|-------------|
| `backend_url` | `http://localhost:8081` | Base URL for backend |

---

## 📝 Test Cases - CRUD Operations

### **1. CREATE - POST /api/risk**

**Request:**
```http
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

**Expected Response (201 Created):**
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

**Validation:**
- ✅ Status code = 201
- ✅ Response contains `id` field
- ✅ All fields match request
- ✅ `createdAt` and `updatedAt` are present

**Postman Test:**
```javascript
pm.test("Status code is 201", function() {
    pm.response.to.have.status(201);
});

pm.test("Response has ID", function() {
    var jsonData = pm.response.json();
    pm.expect(jsonData.id).to.exist;
});
```

---

### **2. GET ALL - GET /api/risk**

**Request:**
```http
GET http://localhost:8081/api/risk
```

**Expected Response (200 OK):**
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

**Validation:**
- ✅ Status code = 200
- ✅ Response is an array
- ✅ Each item has all required fields

**Postman Test:**
```javascript
pm.test("Status code is 200", function() {
    pm.response.to.have.status(200);
});

pm.test("Response is array", function() {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
});
```

---

### **3. GET BY ID - GET /api/risk/{id}**

**Request:**
```http
GET http://localhost:8081/api/risk/1
```

**Expected Response (200 OK):**
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

**Validation:**
- ✅ Status code = 200
- ✅ Returned ID matches requested ID
- ✅ Single object (not array)

**Postman Test:**
```javascript
pm.test("Status code is 200", function() {
    pm.response.to.have.status(200);
});

pm.test("ID matches request", function() {
    var jsonData = pm.response.json();
    pm.expect(jsonData.id).to.equal(1);
});
```

---

### **4. FILTER BY STATUS - GET /api/risk/status/{status}**

**Request:**
```http
GET http://localhost:8081/api/risk/status/ACTIVE
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "status": "ACTIVE",
    ...
  }
]
```

**Valid Status Values:**
- `ACTIVE`
- `MITIGATED`
- `RESOLVED`
- `ARCHIVED`

**Validation:**
- ✅ Status code = 200
- ✅ All returned items have matching status
- ✅ Response is array

**Postman Test:**
```javascript
pm.test("All items have ACTIVE status", function() {
    var jsonData = pm.response.json();
    jsonData.forEach(function(risk) {
        pm.expect(risk.status).to.equal('ACTIVE');
    });
});
```

---

### **5. FILTER BY CATEGORY - GET /api/risk/category/{category}**

**Request:**
```http
GET http://localhost:8081/api/risk/category/Security
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "category": "Security",
    ...
  }
]
```

**Valid Categories:**
- `Security`
- `Operations`
- `Financial`
- `Technical`
- `Compliance`

**Validation:**
- ✅ Status code = 200
- ✅ All returned items have matching category
- ✅ Response is array

---

### **6. SEARCH - GET /api/risk/search?q={searchTerm}**

**Request:**
```http
GET http://localhost:8081/api/risk/search?q=Data
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Data Breach Risk",
    "description": "Risk of unauthorized access to customer data",
    ...
  }
]
```

**Validation:**
- ✅ Status code = 200
- ✅ Results match search term in title or description
- ✅ Response is array

**Postman Test:**
```javascript
pm.test("Search results contain query term", function() {
    var jsonData = pm.response.json();
    jsonData.forEach(function(risk) {
        var text = (risk.title + risk.description).toUpperCase();
        pm.expect(text).to.include('DATA');
    });
});
```

---

### **7. UPDATE - PUT /api/risk/{id}**

**Request:**
```http
PUT http://localhost:8081/api/risk/1
Content-Type: application/json

{
  "title": "Critical Data Breach Risk",
  "riskLevel": "CRITICAL",
  "status": "MITIGATED",
  "description": "Updated risk description"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "title": "Critical Data Breach Risk",
  "description": "Updated risk description",
  "category": "Security",
  "riskLevel": "CRITICAL",
  "status": "MITIGATED",
  "createdAt": "2026-05-02T22:58:51.123456",
  "updatedAt": "2026-05-02T23:00:12.987654"
}
```

**Validation:**
- ✅ Status code = 200
- ✅ Updated fields reflect request values
- ✅ Unchanged fields preserved
- ✅ `updatedAt` timestamp changed
- ✅ `createdAt` timestamp unchanged

**Postman Test:**
```javascript
pm.test("Status code is 200", function() {
    pm.response.to.have.status(200);
});

pm.test("Fields updated correctly", function() {
    var jsonData = pm.response.json();
    pm.expect(jsonData.riskLevel).to.equal('CRITICAL');
    pm.expect(jsonData.status).to.equal('MITIGATED');
});
```

---

### **8. DELETE - DELETE /api/risk/{id}**

**Request:**
```http
DELETE http://localhost:8081/api/risk/1
```

**Expected Response (204 No Content):**
```
(empty body)
```

**Validation:**
- ✅ Status code = 204
- ✅ Response body is empty
- ✅ Subsequent GET on same ID should return 404 (soft delete)

**Postman Test:**
```javascript
pm.test("Status code is 204", function() {
    pm.response.to.have.status(204);
});

pm.test("Response body is empty", function() {
    pm.expect(pm.response.text()).to.be.empty;
});
```

---

## 🔄 Complete Workflow Test

Follow this sequence to test the entire backend workflow:

### **Step 1: Create a Risk**
```bash
POST /api/risk
{
  "title": "Workflow Test Risk",
  "description": "End-to-end workflow test",
  "category": "Security",
  "riskLevel": "HIGH",
  "status": "ACTIVE"
}
```
💾 **Save the returned `id` for next steps**

### **Step 2: Verify Creation - Get by ID**
```bash
GET /api/risk/{id}
```
✅ Confirm fields match creation request

### **Step 3: Get All Risks**
```bash
GET /api/risk
```
✅ Confirm your risk appears in the list

### **Step 4: Filter by Status**
```bash
GET /api/risk/status/ACTIVE
```
✅ Confirm your risk appears in ACTIVE filter

### **Step 5: Filter by Category**
```bash
GET /api/risk/category/Security
```
✅ Confirm your risk appears in Security category

### **Step 6: Search**
```bash
GET /api/risk/search?q=Workflow
```
✅ Confirm your risk appears in search results

### **Step 7: Update Risk**
```bash
PUT /api/risk/{id}
{
  "title": "Updated Workflow Test Risk",
  "riskLevel": "CRITICAL",
  "status": "MITIGATED"
}
```
✅ Verify fields updated

### **Step 8: Verify Update**
```bash
GET /api/risk/{id}
```
✅ Confirm changes persisted

### **Step 9: Delete Risk**
```bash
DELETE /api/risk/{id}
```
✅ Confirm 204 response

### **Step 10: Verify Deletion**
```bash
GET /api/risk/{id}
```
✅ Should return 404 (soft delete, not found)

---

## ⚠️ Error Cases - Validation Testing

### **Test Invalid Input**

#### Missing Required Field
```bash
POST /api/risk
{
  "title": "No Category Risk"
  # Missing category, riskLevel, status
}
```
Expected: 400 Bad Request

#### Invalid Risk Level
```bash
POST /api/risk
{
  "title": "Test",
  "description": "Test",
  "category": "Security",
  "riskLevel": "INVALID_LEVEL",  # Only HIGH, MEDIUM, LOW, CRITICAL
  "status": "ACTIVE"
}
```
Expected: 400 Bad Request

#### Non-existent Resource
```bash
GET /api/risk/999999
```
Expected: 404 Not Found

#### Empty Request Body
```bash
POST /api/risk
{}
```
Expected: 400 Bad Request

#### Malformed JSON
```bash
POST /api/risk
Content-Type: application/json

{ invalid json }
```
Expected: 400 Bad Request

---

## 📊 Data Validation Checklist

For every response, verify:

- [ ] **Status Code** is correct (201 for create, 200 for success, 204 for delete, 4xx for errors)
- [ ] **Response Type** (object vs array)
- [ ] **All Fields Present**: `id`, `title`, `description`, `category`, `riskLevel`, `status`, `createdAt`, `updatedAt`
- [ ] **Field Types**:
  - `id`: Number
  - `title`: String (max 255 chars)
  - `description`: String
  - `category`: String
  - `riskLevel`: String (HIGH/MEDIUM/LOW/CRITICAL)
  - `status`: String (ACTIVE/MITIGATED/RESOLVED/ARCHIVED)
  - `createdAt`: ISO 8601 timestamp
  - `updatedAt`: ISO 8601 timestamp
- [ ] **Field Values**:
  - Title not empty
  - Valid category value
  - Valid risk level value
  - Valid status value
- [ ] **Relationships**:
  - After create, `createdAt` = `updatedAt`
  - After update, `createdAt` unchanged, `updatedAt` changed
  - After delete, resource not retrievable

---

## 🧪 Load Testing

Test backend performance with multiple requests:

### Sequential Creation (5 risks)
```
Loop 5 times:
  POST /api/risk with unique titles
```
**Measure:** Response time per request (target: < 200ms)

### Bulk Retrieval
```
GET /api/risk
```
**Measure:** Response time with all risks (target: < 500ms)

### Concurrent Operations
```
Send 10 parallel POST requests
```
**Measure:** Success rate should be 100%

---

## 🐛 Debugging Tips

### Check Backend Logs
```bash
# If running with mvn spring-boot:run, logs appear in console
# Look for:
# - SQL queries
# - Error stacktraces
# - Validation messages
```

### Database Verification
```sql
-- Connect to PostgreSQL
psql -U postgres -d risk_simulator

-- Check data
SELECT * FROM risk_scenarios;

-- Check timestamps
SELECT id, title, created_at, updated_at FROM risk_scenarios;

-- Verify soft deletes
SELECT id, title, deleted_at FROM risk_scenarios WHERE deleted_at IS NOT NULL;
```

### Postman Console
1. Open Postman Console (bottom of app)
2. Send request
3. View request/response details
4. Check headers and body

### Common Issues

| Issue | Solution |
|-------|----------|
| Connection refused | Verify backend is running on port 8081 |
| 400 Bad Request | Check JSON syntax and required fields |
| 404 Not Found | Verify resource ID exists |
| 500 Internal Error | Check backend logs for exceptions |
| Empty responses | Confirm database is initialized |

---

## ✅ Test Summary

**Total Test Cases:** 8 CRUD operations + error cases

**Required Assertions:**
- [ ] All CRUD operations work
- [ ] Status codes correct
- [ ] Response bodies valid
- [ ] Filtering works
- [ ] Search works
- [ ] Updates persist
- [ ] Deletes complete
- [ ] Errors handled properly

**Time to Run:** ~5 minutes for complete suite

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Postman Documentation](https://learning.postman.com/)
- [REST API Best Practices](https://restfulapi.net/)
- [HTTP Status Codes](https://httpwg.org/specs/rfc7231.html#status.codes)
