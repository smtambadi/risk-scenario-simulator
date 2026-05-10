package com.risk.risk_simulator.service;

import com.risk.risk_simulator.entity.RiskScenario;
import com.risk.risk_simulator.repository.RiskScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Separate service for export operations to avoid class-level @Transactional conflicts
 * in RiskScenarioService. REQUIRES_NEW doesn't work on self-calls in the same bean.
 */
@Service
public class ExportService {

    private final RiskScenarioRepository riskScenarioRepository;

    public ExportService(RiskScenarioRepository riskScenarioRepository) {
        this.riskScenarioRepository = riskScenarioRepository;
    }

    @Transactional(readOnly = true)
    public List<RiskScenario> getAllActiveRisks() {
        return riskScenarioRepository.findAllNonDeleted();
    }
}
