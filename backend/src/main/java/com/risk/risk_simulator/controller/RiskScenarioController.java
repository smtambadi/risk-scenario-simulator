package com.risk.risk_simulator.controller;

import com.risk.risk_simulator.entity.RiskScenario;
import com.risk.risk_simulator.service.RiskScenarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RiskScenarioController {

    private final RiskScenarioService riskScenarioService;

    public RiskScenarioController(RiskScenarioService riskScenarioService) {
        this.riskScenarioService = riskScenarioService;
    }

    @PostMapping
    public ResponseEntity<RiskScenario> createRiskScenario(@RequestBody RiskScenario riskScenario) {
        try {
            RiskScenario createdScenario = riskScenarioService.createRiskScenario(riskScenario);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdScenario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RiskScenario>> getAllRiskScenarios() {
        List<RiskScenario> scenarios = riskScenarioService.getAllRiskScenarios();
        return ResponseEntity.ok(scenarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiskScenario> getRiskScenarioById(@PathVariable Integer id) {
        Optional<RiskScenario> scenario = riskScenarioService.getRiskScenarioById(id);
        return scenario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RiskScenario>> getRiskScenariosByStatus(@PathVariable String status) {
        List<RiskScenario> scenarios = riskScenarioService.getRiskScenariosByStatus(status);
        return ResponseEntity.ok(scenarios);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<RiskScenario>> getRiskScenariosByCategory(@PathVariable String category) {
        List<RiskScenario> scenarios = riskScenarioService.getRiskScenariosByCategory(category);
        return ResponseEntity.ok(scenarios);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RiskScenario>> searchRiskScenarios(@RequestParam String q) {
        List<RiskScenario> scenarios = riskScenarioService.searchRiskScenarios(q);
        return ResponseEntity.ok(scenarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RiskScenario> updateRiskScenario(
            @PathVariable Integer id,
            @RequestBody RiskScenario riskScenarioDetails) {
        try {
            RiskScenario updatedScenario = riskScenarioService.updateRiskScenario(id, riskScenarioDetails);
            return ResponseEntity.ok(updatedScenario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRiskScenario(@PathVariable Integer id) {
        try {
            riskScenarioService.deleteRiskScenario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
