package com.risk.risk_simulator.controller;

import com.risk.risk_simulator.entity.RiskScenario;
import com.risk.risk_simulator.service.RiskScenarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RiskScenarioController {

    private final RiskScenarioService riskScenarioService;

    public RiskScenarioController(RiskScenarioService riskScenarioService) {
        this.riskScenarioService = riskScenarioService;
    }

    /**
     * POST /api/risk - Create new risk scenario with validation
     * Status: 201 CREATED on success, 400 BAD_REQUEST on validation failure
     */
    @PostMapping
    public ResponseEntity<RiskScenario> createRiskScenario(@Valid @RequestBody RiskScenario riskScenario) {
        try {
            RiskScenario createdScenario = riskScenarioService.createRiskScenario(riskScenario);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdScenario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * POST /api/risk/create - Alternative create endpoint
     * Status: 201 CREATED on success, 400 BAD_REQUEST on validation failure
     */
    @PostMapping("/create")
    public ResponseEntity<RiskScenario> createRiskScenarioAlt(@Valid @RequestBody RiskScenario riskScenario) {
        return createRiskScenario(riskScenario);
    }

    /**
     * GET /api/risk/all - Get all risk scenarios with pagination
     * Query params: page=0, size=20, sort=createdAt,desc
     * Status: 200 OK
     */
    @GetMapping("/all")
    public ResponseEntity<Page<RiskScenario>> getAllRiskScenariosPaginated(
            @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<RiskScenario> scenarios = riskScenarioService.getAllRiskScenariosPaginated(pageable);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * GET /api/risk - Get all risk scenarios (non-paginated, legacy)
     * Status: 200 OK
     */
    @GetMapping
    public ResponseEntity<List<RiskScenario>> getAllRiskScenarios() {
        List<RiskScenario> scenarios = riskScenarioService.getAllRiskScenarios();
        return ResponseEntity.ok(scenarios);
    }

    /**
     * GET /api/risk/{id} - Get risk scenario by ID
     * Status: 200 OK or 404 NOT_FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<RiskScenario> getRiskScenarioById(@PathVariable Integer id) {
        Optional<RiskScenario> scenario = riskScenarioService.getRiskScenarioById(id);
        return scenario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * GET /api/risk/status/{status} - Get risk scenarios by status
     * Status: 200 OK
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RiskScenario>> getRiskScenariosByStatus(@PathVariable String status) {
        List<RiskScenario> scenarios = riskScenarioService.getRiskScenariosByStatus(status);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * GET /api/risk/category/{category} - Get risk scenarios by category
     * Status: 200 OK
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<RiskScenario>> getRiskScenariosByCategory(@PathVariable String category) {
        List<RiskScenario> scenarios = riskScenarioService.getRiskScenariosByCategory(category);
        return ResponseEntity.ok(scenarios);
    }

    /**
     * GET /api/risk/search?q={term} - Search risk scenarios by title or description
     * Status: 200 OK or 400 BAD_REQUEST if search term is empty
     */
    @GetMapping("/search")
    public ResponseEntity<List<RiskScenario>> searchRiskScenarios(@RequestParam String q) {
        try {
            List<RiskScenario> scenarios = riskScenarioService.searchRiskScenarios(q);
            return ResponseEntity.ok(scenarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * GET /api/risk/stats - Get dashboard KPIs (total, by status, by category, by risk level)
     * Status: 200 OK
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = riskScenarioService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * PUT /api/risk/{id} - Update risk scenario
     * Status: 200 OK, 404 NOT_FOUND, or 400 BAD_REQUEST
     */
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

    /**
     * DELETE /api/risk/{id} - Soft delete risk scenario
     * Status: 204 NO_CONTENT or 404 NOT_FOUND
     */
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
