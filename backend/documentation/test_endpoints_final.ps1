$baseUrl = "http://localhost:8081"
$authUrl = "$baseUrl/api/auth"
$riskUrl = "$baseUrl/api/risk"

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Risk Scenario Simulator - E2E Tests" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "SETUP: Register User" -ForegroundColor Magenta
$registerPayload = @{
    username = "testuser$(Get-Random -Maximum 10000)"
    email = "test$(Get-Random -Maximum 10000)@example.com"
    password = "TestPassword123!"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-WebRequest -Uri "$authUrl/register" -Method POST -ContentType "application/json" -Body $registerPayload
    Write-Host "SUCCESS Status: $($registerResponse.StatusCode)" -ForegroundColor Green
    $authData = $registerResponse.Content | ConvertFrom-Json
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

Write-Host "SETUP: Login & Get JWT Token" -ForegroundColor Magenta
$loginPayload = @{
    username = $authData.username
    password = "TestPassword123!"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri "$authUrl/login" -Method POST -ContentType "application/json" -Body $loginPayload
    Write-Host "SUCCESS Status: $($loginResponse.StatusCode)" -ForegroundColor Green
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $jwtToken = $loginData.token
    Write-Host "Token: $($jwtToken.Substring(0, 50))..." -ForegroundColor Green
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

$headers = @{
    "Content-Type" = "application/json"
    "Authorization" = "Bearer $jwtToken"
}

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "ENDPOINT TESTS WITH JWT" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$testCount = 0
$passCount = 0
$failCount = 0

Write-Host "TEST 1: GET /api/risk" -ForegroundColor Yellow
$testCount++
try {
    $response = Invoke-WebRequest -Uri $riskUrl -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "SUCCESS Status: $($response.StatusCode)" -ForegroundColor Green
    $risks = $response.Content | ConvertFrom-Json
    Write-Host "Retrieved risks count: $(if ($risks -is [array]) { $risks.Count } else { 1 })" -ForegroundColor Green
    $passCount++
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}
Write-Host ""

Write-Host "TEST 2: POST /api/risk (Create)" -ForegroundColor Yellow
$testCount++
$createPayload = @{
    title = "Test Risk $(Get-Random)"
    description = "Unauthorized access risk"
    category = "Security"
    riskLevel = "HIGH"
    status = "ACTIVE"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $riskUrl -Method POST -Headers $headers -ContentType "application/json" -Body $createPayload
    Write-Host "SUCCESS Status: $($response.StatusCode)" -ForegroundColor Green
    $createdRisk = $response.Content | ConvertFrom-Json
    $newRiskId = $createdRisk.id
    Write-Host "Risk created with ID: $newRiskId" -ForegroundColor Green
    $passCount++
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
    $newRiskId = $null
}
Write-Host ""

if ($newRiskId) {
    Write-Host "TEST 3: GET /api/risk/{id}" -ForegroundColor Yellow
    $testCount++
    try {
        $response = Invoke-WebRequest -Uri "$riskUrl/$newRiskId" -Method GET -Headers $headers -ContentType "application/json"
        Write-Host "SUCCESS Status: $($response.StatusCode)" -ForegroundColor Green
        $risk = $response.Content | ConvertFrom-Json
        Write-Host "Retrieved risk: $($risk.title)" -ForegroundColor Green
        $passCount++
    } catch {
        Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
        $failCount++
    }
    Write-Host ""
}

Write-Host "TEST 4: GET /api/risk/status/ACTIVE" -ForegroundColor Yellow
$testCount++
try {
    $response = Invoke-WebRequest -Uri "$riskUrl/status/ACTIVE" -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "SUCCESS Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Retrieved ACTIVE risks successfully" -ForegroundColor Green
    $passCount++
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}
Write-Host ""

Write-Host "TEST 5: GET /api/risk/category/Security" -ForegroundColor Yellow
$testCount++
try {
    $response = Invoke-WebRequest -Uri "$riskUrl/category/Security" -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "SUCCESS Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Retrieved Security risks successfully" -ForegroundColor Green
    $passCount++
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}
Write-Host ""

Write-Host "TEST 6: GET /api/risk/search?q=Test" -ForegroundColor Yellow
$testCount++
try {
    $response = Invoke-WebRequest -Uri "$riskUrl/search?q=Test" -Method GET -Headers $headers -ContentType "application/json"
    Write-Host "SUCCESS Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Search executed successfully" -ForegroundColor Green
    $passCount++
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
    $failCount++
}
Write-Host ""

if ($newRiskId) {
    Write-Host "TEST 7: PUT /api/risk/{id} (Update)" -ForegroundColor Yellow
    $testCount++
    $updatePayload = @{
        title = "Updated Risk Title"
        riskLevel = "CRITICAL"
        status = "MITIGATED"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest -Uri "$riskUrl/$newRiskId" -Method PUT -Headers $headers -ContentType "application/json" -Body $updatePayload
        Write-Host "SUCCESS Status: $($response.StatusCode)" -ForegroundColor Green
        $updated = $response.Content | ConvertFrom-Json
        Write-Host "Updated - Level: $($updated.riskLevel), Status: $($updated.status)" -ForegroundColor Green
        $passCount++
    } catch {
        Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
        $failCount++
    }
    Write-Host ""
}

if ($newRiskId) {
    Write-Host "TEST 8: DELETE /api/risk/{id}" -ForegroundColor Yellow
    $testCount++
    try {
        $response = Invoke-WebRequest -Uri "$riskUrl/$newRiskId" -Method DELETE -Headers $headers -ContentType "application/json"
        Write-Host "SUCCESS Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Risk deleted successfully" -ForegroundColor Green
        $passCount++
    } catch {
        Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
        $failCount++
    }
    Write-Host ""
}

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Total Tests: $testCount"
Write-Host "Passed: $passCount" -ForegroundColor Green
Write-Host "Failed: $failCount" -ForegroundColor Red

if ($failCount -eq 0 -and $passCount -gt 0) {
    Write-Host ""
    Write-Host "ALL ENDPOINTS WORKING!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "SOME TESTS FAILED!" -ForegroundColor Red
}
Write-Host "=====================================" -ForegroundColor Cyan
