package com.risk.risk_simulator.controller;

import com.risk.risk_simulator.entity.RiskScenario;
import com.risk.risk_simulator.service.AiServiceClient;
import com.risk.risk_simulator.service.ExportService;
import com.risk.risk_simulator.service.RiskScenarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RiskScenarioController {

    private static final Logger logger = LoggerFactory.getLogger(RiskScenarioController.class);

    private final RiskScenarioService riskScenarioService;
    private final AiServiceClient aiServiceClient;
    private final ExportService exportService;

    public RiskScenarioController(RiskScenarioService riskScenarioService,
                                   AiServiceClient aiServiceClient,
                                   ExportService exportService) {
        this.riskScenarioService = riskScenarioService;
        this.aiServiceClient = aiServiceClient;
        this.exportService = exportService;
    }

    /**
     * POST /api/risk - Create new risk scenario
     */
    @PostMapping
    public ResponseEntity<RiskScenario> createRiskScenario(@Valid @RequestBody RiskScenario riskScenario) {
        RiskScenario createdScenario = riskScenarioService.createRiskScenario(riskScenario);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdScenario);
    }

    /**
     * POST /api/risk/create - Alternative create endpoint
     */
    @PostMapping("/create")
    public ResponseEntity<RiskScenario> createRiskScenarioAlt(@Valid @RequestBody RiskScenario riskScenario) {
        return createRiskScenario(riskScenario);
    }

    /**
     * GET /api/risk/all - Get all risk scenarios with pagination and filtering
     * Query params: page, size, sort, search, status, category
     */
    @GetMapping("/all")
    public ResponseEntity<Page<RiskScenario>> getAllRiskScenariosPaginated(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<RiskScenario> scenarios = riskScenarioService.getAllRiskScenariosFiltered(search, status, category, pageable);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * GET /api/risk - Get all risk scenarios (non-paginated)
     */
    @GetMapping
    public ResponseEntity<List<RiskScenario>> getAllRiskScenarios() {
        List<RiskScenario> scenarios = exportService.getAllActiveRisks();
        return ResponseEntity.ok(scenarios);
    }

    /**
     * GET /api/risk/{id} - Get risk scenario by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RiskScenario> getRiskScenarioById(@PathVariable Integer id) {
        Optional<RiskScenario> scenario = riskScenarioService.getRiskScenarioById(id);
        return scenario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * GET /api/risk/status/{status} - Filter by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RiskScenario>> getRiskScenariosByStatus(@PathVariable String status) {
        List<RiskScenario> scenarios = riskScenarioService.getRiskScenariosByStatus(status);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * GET /api/risk/category/{category} - Filter by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<RiskScenario>> getRiskScenariosByCategory(@PathVariable String category) {
        List<RiskScenario> scenarios = riskScenarioService.getRiskScenariosByCategory(category);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * GET /api/risk/search?q={term} - Search
     */
    @GetMapping("/search")
    public ResponseEntity<List<RiskScenario>> searchRiskScenarios(@RequestParam String q) {
        List<RiskScenario> scenarios = riskScenarioService.searchRiskScenarios(q);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * GET /api/risk/stats - Dashboard KPIs
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = riskScenarioService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * PUT /api/risk/{id} - Update risk scenario
     */
    @PutMapping("/{id}")
    public ResponseEntity<RiskScenario> updateRiskScenario(
            @PathVariable Integer id,
            @RequestBody RiskScenario riskScenarioDetails) {
        RiskScenario updatedScenario = riskScenarioService.updateRiskScenario(id, riskScenarioDetails);
        return ResponseEntity.ok(updatedScenario);
    }

    /**
     * DELETE /api/risk/{id} - Soft delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRiskScenario(@PathVariable Integer id) {
        riskScenarioService.deleteRiskScenario(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/risk/export/csv - Export all risks as CSV
     */
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportCSV() {
        List<RiskScenario> risks = exportService.getAllActiveRisks();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Description,Category,Risk Score,Impact,Likelihood,Status,Risk Level,Mitigation Plan,Projected Cost,Created At,Updated At\n");
        for (RiskScenario r : risks) {
            csv.append(r.getId()).append(",");
            csv.append(escapeCsv(r.getTitle())).append(",");
            csv.append(escapeCsv(r.getDescription())).append(",");
            csv.append(escapeCsv(r.getCategory())).append(",");
            csv.append(r.getRiskScore() != null ? r.getRiskScore() : "").append(",");
            csv.append(escapeCsv(r.getImpact())).append(",");
            csv.append(escapeCsv(r.getLikelihood())).append(",");
            csv.append(escapeCsv(r.getStatus())).append(",");
            csv.append(escapeCsv(r.getRiskLevel())).append(",");
            csv.append(escapeCsv(r.getMitigationPlan())).append(",");
            csv.append(r.getProjectedCost() != null ? r.getProjectedCost() : "").append(",");
            csv.append(r.getCreatedAt() != null ? r.getCreatedAt().toString() : "").append(",");
            csv.append(r.getUpdatedAt() != null ? r.getUpdatedAt().toString() : "").append("\n");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=risk_scenarios_export.csv");
        return new ResponseEntity<>(csv.toString(), headers, HttpStatus.OK);
    }

    /**
     * GET /api/risk/ai/report/stream - SSE streaming AI report.
     * Relays real Groq streaming tokens from Flask /generate-report/stream endpoint.
     * Falls back to template-based report if Flask is unreachable.
     */
    @GetMapping(value = "/ai/report/stream", produces = "text/event-stream")
    public ResponseEntity<SseEmitter> streamAiReport() {
        SseEmitter emitter = new SseEmitter(120_000L);  // 2 minute timeout

        CompletableFuture.runAsync(() -> {
            try {
                // Build summary input from actual risks
                List<RiskScenario> risks = exportService.getAllActiveRisks();
                StringBuilder inputBuilder = new StringBuilder();
                inputBuilder.append("Risk Portfolio with ").append(risks.size()).append(" scenarios. ");
                risks.stream().limit(10).forEach(r -> {
                    inputBuilder.append(r.getTitle())
                        .append(" (Score: ").append(r.getRiskScore()).append(", ")
                        .append(r.getCategory()).append(", ")
                        .append(r.getStatus()).append("). ");
                });
                String input = inputBuilder.toString();

                // Try to relay from Flask SSE
                boolean relayed = relayFlaskSSE(emitter, input);
                if (!relayed) {
                    // Fallback: generate template-based report
                    generateFallbackReport(emitter, risks);
                }
            } catch (Exception e) {
                logger.error("Stream report error: {}", e.getMessage());
                emitter.completeWithError(e);
            }
        });

        return ResponseEntity.ok()
            .header("Cache-Control", "no-cache")
            .header("X-Accel-Buffering", "no")
            .body(emitter);
    }

    /**
     * Relay SSE tokens from Flask /generate-report/stream to the client.
     */
    private boolean relayFlaskSSE(SseEmitter emitter, String input) {
        try {
            String flaskUrl = aiServiceClient.getAiServiceUrl() + "/generate-report/stream";

            HttpURLConnection conn = (HttpURLConnection) new URL(flaskUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(60000);

            // Send input
            String jsonBody = "{\"input\": \"" + input.replace("\"", "\\\"") + "\"}";
            conn.getOutputStream().write(jsonBody.getBytes());

            if (conn.getResponseCode() != 200) {
                logger.warn("Flask SSE returned status {}", conn.getResponseCode());
                return false;
            }

            // Read and relay SSE lines
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        emitter.send(SseEmitter.event().data(data));
                        if ("[DONE]".equals(data)) {
                            break;
                        }
                    }
                }
            }
            emitter.complete();
            return true;

        } catch (Exception e) {
            logger.warn("Failed to relay Flask SSE, using fallback: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Fallback template-based report when Flask is unreachable.
     */
    private void generateFallbackReport(SseEmitter emitter, List<RiskScenario> risks) {
        try {
            long total = risks.size();
            long critical = risks.stream().filter(r -> "CRITICAL".equals(r.getStatus())).count();
            long open = risks.stream().filter(r -> "OPEN".equals(r.getStatus())).count();
            double avgScore = risks.stream().mapToDouble(r -> r.getRiskScore() != null ? r.getRiskScore() : 0).average().orElse(0);

            String[] tokens = {
                "# Risk Portfolio Executive Summary\n\n",
                "**Generated:** " + java.time.LocalDateTime.now().toString().substring(0,16) + "\n\n",
                "---\n\n",
                "## Portfolio Overview\n\n",
                "- **Total Scenarios:** " + total + "\n",
                "- **Critical Alerts:** " + critical + "\n",
                "- **Open Risks:** " + open + "\n",
                "- **Avg Risk Score:** " + String.format("%.1f", avgScore) + "/10\n\n",
                "---\n\n",
                "## Risk Assessment\n\n",
                avgScore > 7 ? "⚠️ **HIGH ALERT**: Portfolio average exceeds acceptable threshold (7.0).\n\n"
                            : "✅ Portfolio average within acceptable range.\n\n",
                "## Top Critical Items\n\n",
            };

            for (String token : tokens) {
                emitter.send(SseEmitter.event()
                    .data("{\"token\":\"" + token.replace("\"","\\\"").replace("\n","\\n") + "\"}"));
                Thread.sleep(80);
            }

            // Stream top critical/high risks
            risks.stream()
                .filter(r -> "CRITICAL".equals(r.getStatus()) || "OPEN".equals(r.getStatus()))
                .limit(5)
                .forEach(r -> {
                    try {
                        String line = "- **" + r.getTitle() + "** (Score: " + r.getRiskScore() + "/10, " + r.getCategory() + ")\\n";
                        emitter.send(SseEmitter.event()
                            .data("{\"token\":\"" + line.replace("\"","\\\"") + "\"}"));
                        Thread.sleep(100);
                    } catch (Exception ignored) {}
                });

            String closing = "\n\n---\n\n*Report generated by RISK.SIM Neural Advisor (fallback mode)*";
            emitter.send(SseEmitter.event()
                .data("{\"token\":\"" + closing.replace("\"","\\\"").replace("\n","\\n") + "\"}"));
            Thread.sleep(100);
            emitter.send(SseEmitter.event().data("[DONE]"));
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    // ===== AI Proxy Endpoints =====

    /**
     * POST /api/risk/{id}/ai/describe - Get AI description for a risk
     */
    @PostMapping("/{id}/ai/describe")
    public ResponseEntity<String> aiDescribe(@PathVariable Integer id) {
        Optional<RiskScenario> scenario = riskScenarioService.getRiskScenarioById(id);
        if (scenario.isEmpty()) return ResponseEntity.notFound().build();
        RiskScenario risk = scenario.get();
        String input = risk.getTitle() + ": " + risk.getDescription();
        String result = aiServiceClient.describe(input);
        if (result == null) {
            return ResponseEntity.ok("{\"description\": \"AI service unavailable. The risk '" + risk.getTitle() + "' requires manual analysis.\", \"is_fallback\": true}");
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/risk/{id}/ai/recommend - Get AI recommendations
     */
    @PostMapping("/{id}/ai/recommend")
    public ResponseEntity<String> aiRecommend(@PathVariable Integer id) {
        Optional<RiskScenario> scenario = riskScenarioService.getRiskScenarioById(id);
        if (scenario.isEmpty()) return ResponseEntity.notFound().build();
        RiskScenario risk = scenario.get();
        String input = risk.getTitle() + ": " + risk.getDescription() +
                (risk.getMitigationPlan() != null ? " Current mitigation: " + risk.getMitigationPlan() : "");
        String result = aiServiceClient.recommend(input);
        if (result == null) {
            return ResponseEntity.ok("{\"recommendations\": [{\"action_type\": \"Review\", \"description\": \"AI unavailable. Manual review recommended.\", \"priority\": \"HIGH\"}], \"is_fallback\": true}");
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/risk/{id}/ai/categorise - AI categorise a risk
     */
    @PostMapping("/{id}/ai/categorise")
    public ResponseEntity<String> aiCategorise(@PathVariable Integer id) {
        Optional<RiskScenario> scenario = riskScenarioService.getRiskScenarioById(id);
        if (scenario.isEmpty()) return ResponseEntity.notFound().build();
        RiskScenario risk = scenario.get();
        String input = risk.getTitle() + ": " + risk.getDescription();
        String result = aiServiceClient.categorise(input);
        if (result == null) {
            return ResponseEntity.ok("{\"category\": \"Unknown\", \"confidence\": 0, \"reasoning\": \"AI service unavailable\", \"is_fallback\": true}");
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/risk/{id}/ai/query - RAG query about a risk
     */
    @PostMapping("/{id}/ai/query")
    public ResponseEntity<String> aiQuery(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String question = body.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"question is required\"}");
        }
        String result = aiServiceClient.query(question);
        if (result == null) {
            return ResponseEntity.ok("{\"answer\": \"AI service unavailable. Please try again later.\", \"sources\": [], \"is_fallback\": true}");
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/risk/ai/analyse-document - Analyze a document for risks
     */
    @PostMapping("/ai/analyse-document")
    public ResponseEntity<String> aiAnalyseDocument(@RequestBody Map<String, String> body) {
        String input = body.get("input");
        if (input == null || input.trim().length() < 20) {
            return ResponseEntity.badRequest().body("{\"error\": \"input is required (min 20 chars)\"}");
        }
        String result = aiServiceClient.analyseDocument(input);
        if (result == null) {
            return ResponseEntity.ok("{\"findings\": [], \"summary\": \"AI service unavailable.\", \"is_fallback\": true}");
        }
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/risk/ai/batch-process - Batch process multiple risks
     */
    @PostMapping("/ai/batch-process")
    public ResponseEntity<String> aiBatchProcess(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) body.get("items");
        if (items == null || items.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"items array is required\"}");
        }
        String result = aiServiceClient.batchProcess(items);
        if (result == null) {
            return ResponseEntity.ok("{\"results\": [], \"error\": \"AI service unavailable\", \"is_fallback\": true}");
        }
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/risk/ai/health - Check AI service health
     */
    @GetMapping("/ai/health")
    public ResponseEntity<String> aiHealth() {
        String result = aiServiceClient.healthCheck();
        if (result == null) {
            return ResponseEntity.ok("{\"status\": \"unavailable\", \"message\": \"AI service is not reachable\"}");
        }
        return ResponseEntity.ok(result);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
