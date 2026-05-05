package com.risk.risk_simulator.service;

import com.risk.risk_simulator.entity.RiskScenario;
import com.risk.risk_simulator.exception.InvalidRiskScenarioException;
import com.risk.risk_simulator.exception.ResourceNotFoundException;
import com.risk.risk_simulator.repository.RiskScenarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class RiskScenarioService {

    private final RiskScenarioRepository riskScenarioRepository;

    public RiskScenarioService(RiskScenarioRepository riskScenarioRepository) {
        this.riskScenarioRepository = riskScenarioRepository;
    }

    /**
     * Create new risk scenario with validation
     */
    public RiskScenario createRiskScenario(RiskScenario riskScenario) {
        if (riskScenario.getTitle() == null || riskScenario.getTitle().trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Risk Scenario title cannot be null or empty");
        }
        if (riskScenario.getDescription() == null || riskScenario.getDescription().trim().isEmpty()) {
            throw new InvalidRiskScenarioException("Risk Scenario description cannot be null or empty");
        }
        if (riskScenario.getStatus() == null) {
            riskScenario.setStatus("ACTIVE");
        }
        return riskScenarioRepository.save(riskScenario);
    }

    /**
     * Get all risk scenarios with pagination
     */
    @Transactional(readOnly = true)
    public Page<RiskScenario> getAllRiskScenariosPaginated(Pageable pageable) {
        return riskScenarioRepository.findAllActive(pageable);
    }

    /**
     * Get all risk scenarios (non-paginated, legacy)
     */
    @Transactional(readOnly = true)
    public List<RiskScenario> getAllRiskScenarios() {
        return riskScenarioRepository.findAll();
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
            return riskScenarioRepository.save(existingScenario);
        }).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    /**
     * Delete risk scenario (hard delete)
     */
    public void deleteRiskScenario(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidRiskScenarioException("Invalid Risk Scenario ID: must be a positive integer");
        }
        if (!riskScenarioRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        riskScenarioRepository.deleteById(id);
    }

    /**
     * Get dashboard statistics/KPIs
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Total scenarios
        long totalScenarios = riskScenarioRepository.countActiveScenarios();
        stats.put("totalScenarios", totalScenarios);

        // Scenarios by status
        Map<String, Long> scenariosByStatus = new LinkedHashMap<>();
        List<Object[]> statusResults = riskScenarioRepository.getScenariosByStatus();
        for (Object[] result : statusResults) {
            scenariosByStatus.put((String) result[0], ((Number) result[1]).longValue());
        }
        stats.put("scenariosByStatus", scenariosByStatus);

        // Scenarios by category
        Map<String, Long> scenariosByCategory = new LinkedHashMap<>();
        List<Object[]> categoryResults = riskScenarioRepository.getScenariosByCategory();
        for (Object[] result : categoryResults) {
            scenariosByCategory.put((String) result[0], ((Number) result[1]).longValue());
        }
        stats.put("scenariosByCategory", scenariosByCategory);

        // Scenarios by risk level
        Map<String, Long> scenariosByRiskLevel = new LinkedHashMap<>();
        List<Object[]> riskLevelResults = riskScenarioRepository.getScenariosByRiskLevel();
        for (Object[] result : riskLevelResults) {
            scenariosByRiskLevel.put((String) result[0], ((Number) result[1]).longValue());
        }
        stats.put("scenariosByRiskLevel", scenariosByRiskLevel);

        return stats;
    }
}
