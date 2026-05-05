# Backend E2E Test Script
$baseUrl = "http://localhost:8081/api/risk"

Write-Host "Testing Risk Scenario Simulator Backend" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: GET ALL
Write-Host "[TEST 1] GET /api/risk" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri $baseUrl -Method GET -ContentType "application/json"
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Result: OK - Retrieved $(($response.Content | ConvertFrom-Json | Measure-Object).Count) risks" -ForegroundColor Green
    $firstRisk = $response.Content | ConvertFrom-Json | Select-Object -First 1
    if ($firstRisk) {
        Write-Host "Sample: $($firstRisk | ConvertTo-Json -Depth 1)" -ForegroundColor Gray
        $riskId = $firstRisk.id
    }
} catch {
    Write-Host "Status: ERROR" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 2: CREATE
Write-Host "[TEST 2] POST /api/risk (Create)" -ForegroundColor Yellow
$newRisk = @{
    title = "Test Risk $(Get-Random)"
    description = "Unauthorized access risk"
    category = "Security"
    riskLevel = "HIGH"
    status = "ACTIVE"
}

try {
    $response = Invoke-WebRequest -Uri $baseUrl -Method POST -ContentType "application/json" -Body ($newRisk | ConvertTo-Json)
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Result: OK - Risk created" -ForegroundColor Green
    $createdRisk = $response.Content | ConvertFrom-Json
    Write-Host "Created Risk: $($createdRisk | ConvertTo-Json)" -ForegroundColor Gray
    $newRiskId = $createdRisk.id
} catch {
    Write-Host "Status: ERROR" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 3: GET BY ID
Write-Host "[TEST 3] GET /api/risk/{id}" -ForegroundColor Yellow
if ($newRiskId) {
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/$newRiskId" -Method GET -ContentType "application/json"
        Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Result: OK - Retrieved risk ID $newRiskId" -ForegroundColor Green
        Write-Host "Data: $($response.Content | ConvertFrom-Json | ConvertTo-Json)" -ForegroundColor Gray
    } catch {
        Write-Host "Status: ERROR" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "Status: SKIPPED - No risk ID available" -ForegroundColor Yellow
}
Write-Host ""

# Test 4: FILTER BY STATUS
Write-Host "[TEST 4] GET /api/risk/status/ACTIVE" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/status/ACTIVE" -Method GET -ContentType "application/json"
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    $count = ($response.Content | ConvertFrom-Json | Measure-Object).Count
    Write-Host "Result: OK - Retrieved $count active risks" -ForegroundColor Green
} catch {
    Write-Host "Status: ERROR" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 5: FILTER BY CATEGORY
Write-Host "[TEST 5] GET /api/risk/category/Security" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/category/Security" -Method GET -ContentType "application/json"
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    $count = ($response.Content | ConvertFrom-Json | Measure-Object).Count
    Write-Host "Result: OK - Retrieved $count security risks" -ForegroundColor Green
} catch {
    Write-Host "Status: ERROR" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 6: SEARCH
Write-Host "[TEST 6] GET /api/risk/search?q=Test" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/search?q=Test" -Method GET -ContentType "application/json"
    Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
    $count = ($response.Content | ConvertFrom-Json | Measure-Object).Count
    Write-Host "Result: OK - Found $count matching risks" -ForegroundColor Green
} catch {
    Write-Host "Status: ERROR" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Test 7: UPDATE
Write-Host "[TEST 7] PUT /api/risk/{id} (Update)" -ForegroundColor Yellow
if ($newRiskId) {
    $updateData = @{
        title = "Updated Risk Title"
        riskLevel = "CRITICAL"
        status = "MITIGATED"
    }
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/$newRiskId" -Method PUT -ContentType "application/json" -Body ($updateData | ConvertTo-Json)
        Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Result: OK - Risk updated" -ForegroundColor Green
        Write-Host "Updated Data: $($response.Content | ConvertFrom-Json | ConvertTo-Json)" -ForegroundColor Gray
    } catch {
        Write-Host "Status: ERROR" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "Status: SKIPPED - No risk ID available" -ForegroundColor Yellow
}
Write-Host ""

# Test 8: DELETE
Write-Host "[TEST 8] DELETE /api/risk/{id}" -ForegroundColor Yellow
if ($newRiskId) {
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/$newRiskId" -Method DELETE -ContentType "application/json"
        Write-Host "Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Result: OK - Risk deleted" -ForegroundColor Green
    } catch {
        Write-Host "Status: ERROR" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "Status: SKIPPED - No risk ID available" -ForegroundColor Yellow
}
Write-Host ""

Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "Testing Complete!" -ForegroundColor Cyan
