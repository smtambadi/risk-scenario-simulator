package com.risk.risk_simulator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Client to call Flask AI microservice endpoints.
 * Returns null gracefully on any error (30s timeout).
 */
@Service
public class AiServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AiServiceClient.class);

    private final RestTemplate restTemplate;
    private final String aiServiceUrl;

    public AiServiceClient(@Value("${ai.service.url:http://localhost:5000}") String aiServiceUrl) {
        this.aiServiceUrl = aiServiceUrl;
        // Set timeouts via factory
        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(java.time.Duration.ofSeconds(10));
        factory.setReadTimeout(java.time.Duration.ofSeconds(30));  // increased for streaming/batch
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * POST /describe — Generate AI description for a risk scenario
     */
    public String describe(String input) {
        return callAiEndpoint("/describe", Map.of("input", input));
    }

    /**
     * POST /recommend — Get 3 actionable recommendations
     */
    public String recommend(String input) {
        return callAiEndpoint("/recommend", Map.of("input", input));
    }

    /**
     * POST /categorise — Classify risk into a category
     */
    public String categorise(String input) {
        return callAiEndpoint("/categorise", Map.of("input", input));
    }

    /**
     * POST /query — RAG query with ChromaDB context
     */
    public String query(String question) {
        return callAiEndpoint("/query", Map.of("question", question));
    }

    /**
     * POST /generate-report — Generate a full risk report
     */
    public String generateReport(String input) {
        return callAiEndpoint("/generate-report", Map.of("input", input));
    }

    /**
     * POST /analyse-document — Analyze a document for risks
     */
    public String analyseDocument(String input) {
        return callAiEndpoint("/analyse-document", Map.of("input", input));
    }

    /**
     * POST /batch-process — Batch analyze multiple risk items
     */
    public String batchProcess(List<String> items) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
                    Map.of("items", items), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServiceUrl + "/batch-process", entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("AI batch-process call failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * GET /health — Check AI service health
     */
    public String healthCheck() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    aiServiceUrl + "/health", String.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("AI health check failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get the base URL of the AI service (used for SSE relay)
     */
    public String getAiServiceUrl() {
        return aiServiceUrl;
    }

    /**
     * Generic POST call to AI endpoint with graceful null return on error
     */
    private String callAiEndpoint(String path, Map<String, String> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServiceUrl + path, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("AI service call to {} failed: {}", path, e.getMessage());
            return null;
        }
    }
}
