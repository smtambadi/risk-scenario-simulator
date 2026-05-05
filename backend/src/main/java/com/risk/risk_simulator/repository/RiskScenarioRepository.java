package com.risk.risk_simulator.repository;

import com.risk.risk_simulator.entity.RiskScenario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskScenarioRepository extends JpaRepository<RiskScenario, Integer> {

    // ===== Paginated Queries =====
    @Query("SELECT r FROM RiskScenario r ORDER BY r.createdAt DESC")
    Page<RiskScenario> findAllActive(Pageable pageable);

    // ===== Status & Category Queries =====
    List<RiskScenario> findByStatus(String status);

    List<RiskScenario> findByCategory(String category);

    // ===== Search Query =====
    @Query("SELECT r FROM RiskScenario r WHERE " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<RiskScenario> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);

    // ===== Date Range Query =====
    List<RiskScenario> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // ===== Stats/Dashboard Queries =====
    @Query("SELECT COUNT(r) FROM RiskScenario r")
    long countActiveScenarios();

    @Query("SELECT r.status, COUNT(r) FROM RiskScenario r GROUP BY r.status")
    List<Object[]> getScenariosByStatus();

    @Query("SELECT r.category, COUNT(r) FROM RiskScenario r GROUP BY r.category")
    List<Object[]> getScenariosByCategory();

    @Query("SELECT r.riskLevel, COUNT(r) FROM RiskScenario r GROUP BY r.riskLevel")
    List<Object[]> getScenariosByRiskLevel();
}
