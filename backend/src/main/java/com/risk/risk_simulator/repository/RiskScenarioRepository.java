package com.risk.risk_simulator.repository;

import com.risk.risk_simulator.entity.RiskScenario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskScenarioRepository extends JpaRepository<RiskScenario, Integer>,
        JpaSpecificationExecutor<RiskScenario> {

    // ===== Paginated Queries (exclude soft-deleted) =====
    @Query("SELECT r FROM RiskScenario r WHERE r.deleted = false OR r.deleted IS NULL ORDER BY r.createdAt DESC")
    Page<RiskScenario> findAllActive(Pageable pageable);

    // ===== Status & Category Queries =====
    List<RiskScenario> findByStatus(String status);

    List<RiskScenario> findByCategory(String category);

    // ===== Search Query =====
    @Query("SELECT r FROM RiskScenario r WHERE r.deleted = false OR r.deleted IS NULL AND " +
           "(r.title LIKE CONCAT('%', :searchTerm, '%') OR " +
           "r.description LIKE CONCAT('%', :searchTerm, '%'))")
    List<RiskScenario> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);

    // ===== Date Range Query =====
    List<RiskScenario> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // ===== Stats/Dashboard Queries (exclude soft-deleted) =====
    @Query("SELECT COUNT(r) FROM RiskScenario r WHERE r.deleted = false OR r.deleted IS NULL")
    long countActiveScenarios();

    @Query("SELECT r.status, COUNT(r) FROM RiskScenario r WHERE r.deleted = false OR r.deleted IS NULL GROUP BY r.status")
    List<Object[]> getScenariosByStatus();

    @Query("SELECT r.category, COUNT(r) FROM RiskScenario r WHERE r.deleted = false OR r.deleted IS NULL GROUP BY r.category")
    List<Object[]> getScenariosByCategory();

    @Query("SELECT r.riskLevel, COUNT(r) FROM RiskScenario r WHERE r.deleted = false OR r.deleted IS NULL GROUP BY r.riskLevel")
    List<Object[]> getScenariosByRiskLevel();

    @Query("SELECT COALESCE(SUM(r.projectedCost), 0) FROM RiskScenario r WHERE r.deleted = false OR r.deleted IS NULL")
    long getTotalExposure();

    @Query("SELECT COALESCE(AVG(r.riskScore), 0) FROM RiskScenario r WHERE r.deleted = false OR r.deleted IS NULL")
    double getAverageRiskScore();

    @Query("SELECT COUNT(r) FROM RiskScenario r WHERE (r.deleted = false OR r.deleted IS NULL) AND r.status = 'OPEN'")
    long countOpenRisks();

    @Query("SELECT COUNT(r) FROM RiskScenario r WHERE (r.deleted = false OR r.deleted IS NULL) AND r.status = 'CRITICAL'")
    long countCriticalRisks();

    // ===== Non-paginated export query =====
    @Query("SELECT r FROM RiskScenario r WHERE r.deleted IS NULL OR r.deleted = false ORDER BY r.id ASC")
    List<RiskScenario> findAllNonDeleted();
}
