Write-Host "================================" -ForegroundColor Cyan
Write-Host "Risk Scenario Simulator - E2E Tests" -ForegroundColor Cyan
Write-Host "Backend Endpoint Testing" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8081/api/risk"
$headers = @{
    "Content-Type" = "application/json"
    "Accept" = "application/json"
}

$testResults = @()

# Test 1: GET ALL - GET /api/risk
Write-Host "TEST 1: GET ALL - GET /api/risk" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri $baseUrl -Method GET -Headers $headers -SkipHttpErrorCheck
    $testResults += @{
        Name = "GET ALL"
        Endpoint = "GET /api/risk"
        StatusCode = $response.StatusCode
        Status = if ($response.StatusCode -eq 200) { "PASS" } else { "FAIL" }
        Details = "$($response.StatusCode) - Expected 200"
    }
    Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 2)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
    $testResults += @{
        Name = "GET ALL"
        Endpoint = "GET /api/risk"
        StatusCode = "ERROR"
        Status = "FAIL"
        Details = $_.Exception.Message
    }
}
Write-Host ""

# Test 2: CREATE - POST /api/risk
Write-Host "TEST 2: CREATE - POST /api/risk" -ForegroundColor Yellow
$createPayload = @{
    title = "Test Risk - $(Get-Random)"
    description = "Risk of unauthorized access to customer data"
    category = "Security"
    riskLevel = "HIGH"
    status = "ACTIVE"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri $baseUrl -Method POST -Headers $headers -Body $createPayload -SkipHttpErrorCheck
    $testResults += @{
        Name = "CREATE"
        Endpoint = "POST /api/risk"
        StatusCode = $response.StatusCode
        Status = if ($response.StatusCode -eq 201) { "PASS" } else { "FAIL" }
        Details = "$($response.StatusCode) - Expected 201"
    }
    Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    
    if ($response.StatusCode -eq 201) {
        $riskData = $response.Content | ConvertFrom-Json
        $riskId = $riskData.id
        Write-Host "Created Risk ID: $riskId" -ForegroundColor Cyan
        Write-Host "Response: $($response.Content | ConvertFrom-Json | ConvertTo-Json)" -ForegroundColor Gray
    }
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
    $testResults += @{
        Name = "CREATE"
        Endpoint = "POST /api/risk"
        StatusCode = "ERROR"
        Status = "FAIL"
        Details = $_.Exception.Message
    }
}
Write-Host ""

# Test 3: GET BY ID - GET /api/risk/{id}
Write-Host "TEST 3: GET BY ID - GET /api/risk/{id}" -ForegroundColor Yellow
if ($riskId) {
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/$riskId" -Method GET -Headers $headers -SkipHttpErrorCheck
        $testResults += @{
            Name = "GET BY ID"
            Endpoint = "GET /api/risk/$riskId"
            StatusCode = $response.StatusCode
            Status = if ($response.StatusCode -eq 200) { "PASS" } else { "FAIL" }
            Details = "$($response.StatusCode) - Expected 200"
        }
        Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Response: $($response.Content | ConvertFrom-Json | ConvertTo-Json)" -ForegroundColor Gray
    } catch {
        Write-Host "❌ Error: $_" -ForegroundColor Red
        $testResults += @{
            Name = "GET BY ID"
            Endpoint = "GET /api/risk/$riskId"
            StatusCode = "ERROR"
            Status = "FAIL"
            Details = $_.Exception.Message
        }
    }
} else {
    Write-Host "⚠️  Skipped (no risk ID available)" -ForegroundColor Yellow
}
Write-Host ""

# Test 4: FILTER BY STATUS - GET /api/risk/status/ACTIVE
Write-Host "TEST 4: FILTER BY STATUS - GET /api/risk/status/ACTIVE" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/status/ACTIVE" -Method GET -Headers $headers -SkipHttpErrorCheck
    $testResults += @{
        Name = "FILTER BY STATUS"
        Endpoint = "GET /api/risk/status/ACTIVE"
        StatusCode = $response.StatusCode
        Status = if ($response.StatusCode -eq 200) { "PASS" } else { "FAIL" }
        Details = "$($response.StatusCode) - Expected 200"
    }
    Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 1)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
    $testResults += @{
        Name = "FILTER BY STATUS"
        Endpoint = "GET /api/risk/status/ACTIVE"
        StatusCode = "ERROR"
        Status = "FAIL"
        Details = $_.Exception.Message
    }
}
Write-Host ""

# Test 5: FILTER BY CATEGORY - GET /api/risk/category/Security
Write-Host "TEST 5: FILTER BY CATEGORY - GET /api/risk/category/Security" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/category/Security" -Method GET -Headers $headers -SkipHttpErrorCheck
    $testResults += @{
        Name = "FILTER BY CATEGORY"
        Endpoint = "GET /api/risk/category/Security"
        StatusCode = $response.StatusCode
        Status = if ($response.StatusCode -eq 200) { "PASS" } else { "FAIL" }
        Details = "$($response.StatusCode) - Expected 200"
    }
    Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 1)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
    $testResults += @{
        Name = "FILTER BY CATEGORY"
        Endpoint = "GET /api/risk/category/Security"
        StatusCode = "ERROR"
        Status = "FAIL"
        Details = $_.Exception.Message
    }
}
Write-Host ""

# Test 6: SEARCH - GET /api/risk/search?q=Data
Write-Host "TEST 6: SEARCH - GET /api/risk/search?q=Test" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/search?q=Test" -Method GET -Headers $headers -SkipHttpErrorCheck
    $testResults += @{
        Name = "SEARCH"
        Endpoint = "GET /api/risk/search?q=Test"
        StatusCode = $response.StatusCode
        Status = if ($response.StatusCode -eq 200) { "PASS" } else { "FAIL" }
        Details = "$($response.StatusCode) - Expected 200"
    }
    Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 1)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
    $testResults += @{
        Name = "SEARCH"
        Endpoint = "GET /api/risk/search?q=Test"
        StatusCode = "ERROR"
        Status = "FAIL"
        Details = $_.Exception.Message
    }
}
Write-Host ""

# Test 7: UPDATE - PUT /api/risk/{id}
Write-Host "TEST 7: UPDATE - PUT /api/risk/{id}" -ForegroundColor Yellow
if ($riskId) {
    $updatePayload = @{
        title = "Updated Test Risk"
        riskLevel = "CRITICAL"
        status = "MITIGATED"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/$riskId" -Method PUT -Headers $headers -Body $updatePayload -SkipHttpErrorCheck
        $testResults += @{
            Name = "UPDATE"
            Endpoint = "PUT /api/risk/$riskId"
            StatusCode = $response.StatusCode
            Status = if ($response.StatusCode -eq 200) { "PASS" } else { "FAIL" }
            Details = "$($response.StatusCode) - Expected 200"
        }
        Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Response: $($response.Content | ConvertFrom-Json | ConvertTo-Json)" -ForegroundColor Gray
    } catch {
        Write-Host "❌ Error: $_" -ForegroundColor Red
        $testResults += @{
            Name = "UPDATE"
            Endpoint = "PUT /api/risk/$riskId"
            StatusCode = "ERROR"
            Status = "FAIL"
            Details = $_.Exception.Message
        }
    }
} else {
    Write-Host "⚠️  Skipped (no risk ID available)" -ForegroundColor Yellow
}
Write-Host ""

# Test 8: DELETE - DELETE /api/risk/{id}
Write-Host "TEST 8: DELETE - DELETE /api/risk/{id}" -ForegroundColor Yellow
if ($riskId) {
    try {
        $response = Invoke-WebRequest -Uri "$baseUrl/$riskId" -Method DELETE -Headers $headers -SkipHttpErrorCheck
        $testResults += @{
            Name = "DELETE"
            Endpoint = "DELETE /api/risk/$riskId"
            StatusCode = $response.StatusCode
            Status = if ($response.StatusCode -eq 204) { "PASS" } else { "FAIL" }
            Details = "$($response.StatusCode) - Expected 204"
        }
        Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    } catch {
        Write-Host "❌ Error: $_" -ForegroundColor Red
        $testResults += @{
            Name = "DELETE"
            Endpoint = "DELETE /api/risk/$riskId"
            StatusCode = "ERROR"
            Status = "FAIL"
            Details = $_.Exception.Message
        }
    }
} else {
    Write-Host "⚠️  Skipped (no risk ID available)" -ForegroundColor Yellow
}
Write-Host ""

# Summary Report
Write-Host "================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY REPORT" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

$passCount = ($testResults | Where-Object { $_.Status -eq "PASS" }).Count
$failCount = ($testResults | Where-Object { $_.Status -eq "FAIL" }).Count
$totalCount = $testResults.Count

$testResults | Format-Table -Property Name, Endpoint, StatusCode, Status, Details -AutoSize

Write-Host ""
Write-Host "TOTAL TESTS: $totalCount" -ForegroundColor White
Write-Host "PASSED: $passCount" -ForegroundColor Green
Write-Host "FAILED: $failCount" -ForegroundColor Red
Write-Host ""

if ($failCount -eq 0 -and $passCount -gt 0) {
    Write-Host "✅ ALL TESTS PASSED!" -ForegroundColor Green
} elseif ($failCount -gt 0) {
    Write-Host "❌ SOME TESTS FAILED!" -ForegroundColor Red
} else {
    Write-Host "⚠️  NO TESTS EXECUTED" -ForegroundColor Yellow
}

Write-Host "================================" -ForegroundColor Cyan
