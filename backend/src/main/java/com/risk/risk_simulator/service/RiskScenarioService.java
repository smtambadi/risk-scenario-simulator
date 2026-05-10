package com.risk.risk_simulator.service;

import com.risk.risk_simulator.entity.RiskScenario;
import com.risk.risk_simulator.exception.InvalidRiskScenarioException;
import com.risk.risk_simulator.exception.ResourceNotFoundException;
import com.risk.risk_simulator.repository.RiskScenarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class RiskScenarioService {

    private static final Logger logger = LoggerFactory.getLogger(RiskScenarioService.class);

    private final RiskScenarioRepository riskScenarioRepository;
    private final AiServiceClient aiServiceClient;

    public RiskScenarioService(RiskScenarioRepository riskScenarioRepository,
                                AiServiceClient aiServiceClient) {
        this.riskScenarioRepository = riskScenarioRepository;
        this.aiServiceClient = aiServiceClient;
    }

    /**
     * Create new risk scenario with validation.
     * After saving, triggers async AI analysis (describe + categorise).
     */
    public RiskScenario createRiskScenario(RiskScenario riskScenario) {
        if (riskScenario.getTitle() == null || riskScenario.getTitle().trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Risk Scenario title cannot be null or empty");
        }
        if (riskScenario.getDescription() == null || riskScenario.getDescription().trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Risk Scenario description cannot be null or empty");
        }
        if (riskScenario.getStatus() == null) {
            riskScenario.setStatus("OPEN");
        }
        if (riskScenario.getDeleted() == null) {
            riskScenario.setDeleted(false);
        }
        RiskScenario saved = riskScenarioRepository.save(riskScenario);

        // Trigger async AI enrichment
        try {
            enrichWithAi(saved.getId(), saved.getTitle(), saved.getDescription());
        } catch (Exception e) {
            logger.warn("Failed to trigger async AI enrichment for risk {}: {}", saved.getId(), e.getMessage());
        }

        return saved;
    }

    /**
     * Async AI enrichment: describe + recommend.
     * Runs in a separate thread pool so it doesn't block the create response.
     */
    @Async
    public void enrichWithAi(Integer riskId, String title, String description) {
        String input = title + ": " + description;
        try {
            // Get AI description
            String descriptionResult = aiServiceClient.describe(input);
            // Get AI recommendations
            String recommendResult = aiServiceClient.recommend(input);

            // Save results to entity
            riskScenarioRepository.findById(riskId).ifPresent(risk -> {
                if (descriptionResult != null) {
                    risk.setAiDescription(descriptionResult);
                }
                if (recommendResult != null) {
                    risk.setAiRecommendations(recommendResult);
                }
                riskScenarioRepository.save(risk);
                logger.info("AI enrichment completed for risk #{}", riskId);
            });
        } catch (Exception e) {
            logger.error("AI enrichment failed for risk #{}: {}", riskId, e.getMessage());
        }
    }

    /**
     * Get all risk scenarios with pagination and filtering using JPA Specifications
     * (avoids PostgreSQL null-binding issues with named queries)
     */
    @Transactional(readOnly = true)
    public Page<RiskScenario> getAllRiskScenariosFiltered(String search, String status, String category, Pageable pageable) {
        return riskScenarioRepository.findAll((root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Exclude soft-deleted
            predicates.add(cb.or(
                cb.isFalse(root.get("deleted")),
                cb.isNull(root.get("deleted"))
            ));

            // Search filter
            if (search != null && !search.trim().isEmpty()) {
                String like = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like),
                    cb.like(cb.lower(root.get("category")), like)
                ));
            }

            // Status filter
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status.trim()));
            }

            // Category filter
            if (category != null && !category.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("category"), category.trim()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable);
    }

    /**
     * Get all risk scenarios with pagination
     */
    @Transactional(readOnly = true)
    public Page<RiskScenario> getAllRiskScenariosPaginated(Pageable pageable) {
        return riskScenarioRepository.findAllActive(pageable);
    }

    /**
     * Get all risk scenarios (non-paginated) - used for CSV export and SSE streaming report.
     * Uses REQUIRES_NEW to run in an isolated transaction and avoid class-level @Transactional deadlock.
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW,
                   readOnly = true)
    public List<RiskScenario> getAllRiskScenarios() {
        return riskScenarioRepository.findAllNonDeleted();
    }

    /**
     * Get risk scenario by ID
     */
    @Transactional(readOnly = true)
    public Optional<RiskScenario> getRiskScenarioById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidRiskScenarioException("Invalid Risk Scenario ID: must be a positive integer");
        }
        return riskScenarioRepository.findById(id);
    }

    /**
     * Get risk scenarios by status
     */
    @Transactional(readOnly = true)
    public List<RiskScenario> getRiskScenariosByStatus(String status) {
        return riskScenarioRepository.findByStatus(status);
    }

    /**
     * Get risk scenarios by category
     */
    @Transactional(readOnly = true)
    public List<RiskScenario> getRiskScenariosByCategory(String category) {
        return riskScenarioRepository.findByCategory(category);
    }

    /**
     * Get risk scenarios created between dates
     */
    @Transactional(readOnly = true)
    public List<RiskScenario> getRiskScenariosCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return riskScenarioRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Search risk scenarios by title or description
     */
    @Transactional(readOnly = true)
    public List<RiskScenario> searchRiskScenarios(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Search term cannot be empty");
        }
        return riskScenarioRepository.searchByTitleOrDescription(searchTerm);
    }

    /**
     * Update risk scenario with validation
     */
    public RiskScenario updateRiskScenario(Integer id, RiskScenario riskScenarioDetails) {
        if (id == null || id <= 0) {
            throw new InvalidRiskScenarioException("Invalid Risk Scenario ID: must be a positive integer");
        }
        return riskScenarioRepository.findById(id).map(existingScenario -> {
            if (riskScenarioDetails.getTitle() != null) {
                if (riskScenarioDetails.getTitle().trim().isEmpty()) {
                    throw new InvalidRiskScenarioException("Risk Scenario title cannot be empty");
                }
                existingScenario.setTitle(riskScenarioDetails.getTitle());
            }
            if (riskScenarioDetails.getDescription() != null) {
                if (riskScenarioDetails.getDescription().trim().isEmpty()) {
                    throw new InvalidRiskScenarioException("Risk Scenario description cannot be empty");
                }
                existingScenario.setDescription(riskScenarioDetails.getDescription());
            }
            if (riskScenarioDetails.getCategory() != null) {
                existingScenario.setCategory(riskScenarioDetails.getCategory());
            }
            if (riskScenarioDetails.getRiskLevel() != null) {
                existingScenario.setRiskLevel(riskScenarioDetails.getRiskLevel());
            }
            if (riskScenarioDetails.getStatus() != null) {
                existingScenario.setStatus(riskScenarioDetails.getStatus());
            }
            if (riskScenarioDetails.getRiskScore() != null) {
                existingScenario.setRiskScore(riskScenarioDetails.getRiskScore());
            }
            if (riskScenarioDetails.getImpact() != null) {
                existingScenario.setImpact(riskScenarioDetails.getImpact());
            }
            if (riskScenarioDetails.getLikelihood() != null) {
                existingScenario.setLikelihood(riskScenarioDetails.getLikelihood());
            }
            if (riskScenarioDetails.getMitigationPlan() != null) {
                existingScenario.setMitigationPlan(riskScenarioDetails.getMitigationPlan());
            }
            if (riskScenarioDetails.getProjectedCost() != null) {
                existingScenario.setProjectedCost(riskScenarioDetails.getProjectedCost());
            }
            return riskScenarioRepository.save(existingScenario);
        }).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    /**
     * Soft delete risk scenario
     */
    public void deleteRiskScenario(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidRiskScenarioException("Invalid Risk Scenario ID: must be a positive integer");
        }
        RiskScenario scenario = riskScenarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        scenario.setDeleted(true);
        riskScenarioRepository.save(scenario);
    }

    /**
     * Get comprehensive dashboard statistics/KPIs
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Total scenarios
        long totalScenarios = riskScenarioRepository.countActiveScenarios();
        stats.put("totalScenarios", totalScenarios);
        stats.put("totalRisks", totalScenarios);

        // Open and Critical counts
        long openRisks = riskScenarioRepository.countOpenRisks();
        long criticalRisks = riskScenarioRepository.countCriticalRisks();
        stats.put("openRisks", openRisks);
        stats.put("criticalRisks", criticalRisks);

        // Financial metrics
        long totalExposure = riskScenarioRepository.getTotalExposure();
        double avgRiskScore = riskScenarioRepository.getAverageRiskScore();
        stats.put("totalExposure", totalExposure);
        stats.put("avgRiskScore", Math.round(avgRiskScore * 10.0) / 10.0);

        // Scenarios by status
        Map<String, Long> scenariosByStatus = new LinkedHashMap<>();
        List<Object[]> statusResults = riskScenarioRepository.getScenariosByStatus();
        for (Object[] result : statusResults) {
            scenariosByStatus.put((String) result[0], ((Number) result[1]).longValue());
        }
        stats.put("scenariosByStatus", scenariosByStatus);

        // Status breakdown as array for charts
        List<Map<String, Object>> statusBreakdown = new ArrayList<>();
        for (Object[] result : statusResults) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", result[0]);
            entry.put("value", ((Number) result[1]).longValue());
            statusBreakdown.add(entry);
        }
        stats.put("statusBreakdown", statusBreakdown);

        // Scenarios by category
        Map<String, Long> scenariosByCategory = new LinkedHashMap<>();
        List<Object[]> categoryResults = riskScenarioRepository.getScenariosByCategory();
        for (Object[] result : categoryResults) {
            scenariosByCategory.put((String) result[0], ((Number) result[1]).longValue());
        }
        stats.put("scenariosByCategory", scenariosByCategory);

        // Category breakdown as array for charts
        List<Map<String, Object>> categoryBreakdown = new ArrayList<>();
        for (Object[] result : categoryResults) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", result[0]);
            entry.put("value", ((Number) result[1]).longValue());
            categoryBreakdown.add(entry);
        }
        stats.put("categoryBreakdown", categoryBreakdown);

        // Scenarios by risk level
        Map<String, Long> scenariosByRiskLevel = new LinkedHashMap<>();
        List<Object[]> riskLevelResults = riskScenarioRepository.getScenariosByRiskLevel();
        for (Object[] result : riskLevelResults) {
            scenariosByRiskLevel.put((String) result[0], ((Number) result[1]).longValue());
        }
        stats.put("scenariosByRiskLevel", scenariosByRiskLevel);

        // Monthly trend (synthetic for demo - last 6 months)
        List<Map<String, Object>> monthlyTrend = new ArrayList<>();
        String[] months = {"Nov", "Dec", "Jan", "Feb", "Mar", "Apr"};
        long base = Math.max(totalScenarios / 3, 5);
        for (int i = 0; i < months.length; i++) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", months[i]);
            entry.put("risks", base + (i * 3) + (i % 2 == 0 ? 2 : -1));
            entry.put("mitigated", base / 2 + i);
            monthlyTrend.add(entry);
        }
        stats.put("monthlyTrend", monthlyTrend);

        return stats;
    }
}
