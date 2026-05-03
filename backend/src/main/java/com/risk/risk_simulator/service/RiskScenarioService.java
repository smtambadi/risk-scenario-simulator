package com.risk.risk_simulator.service;

import com.risk.risk_simulator.entity.RiskScenario;
import com.risk.risk_simulator.repository.RiskScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RiskScenarioService {

    private final RiskScenarioRepository riskScenarioRepository;

    public RiskScenarioService(RiskScenarioRepository riskScenarioRepository) {
        this.riskScenarioRepository = riskScenarioRepository;
    }

    public RiskScenario createRiskScenario(RiskScenario riskScenario) {
        if (riskScenario.getStatus() == null) {
            riskScenario.setStatus("ACTIVE");
        }
        return riskScenarioRepository.save(riskScenario);
    }

    @Transactional(readOnly = true)
    public List<RiskScenario> getAllRiskScenarios() {
        return riskScenarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<RiskScenario> getRiskScenarioById(Integer id) {
        return riskScenarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<RiskScenario> getRiskScenariosByStatus(String status) {
        return riskScenarioRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<RiskScenario> getRiskScenariosByCategory(String category) {
        return riskScenarioRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<RiskScenario> getRiskScenariosCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return riskScenarioRepository.findByCreatedAtBetween(start, end);
    }

    @Transactional(readOnly = true)
    public List<RiskScenario> searchRiskScenarios(String searchTerm) {
        return riskScenarioRepository.searchByTitleOrDescription(searchTerm);
    }

    public RiskScenario updateRiskScenario(Integer id, RiskScenario riskScenarioDetails) {
        return riskScenarioRepository.findById(id).map(existingScenario -> {
            if (riskScenarioDetails.getTitle() != null) {
                existingScenario.setTitle(riskScenarioDetails.getTitle());
            }
            if (riskScenarioDetails.getDescription() != null) {
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
        }).orElseThrow(() -> new IllegalArgumentException("Risk Scenario not found with id: " + id));
    }

    public void deleteRiskScenario(Integer id) {
        if (!riskScenarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Risk Scenario not found with id: " + id);
        }
        riskScenarioRepository.deleteById(id);
    }
}
