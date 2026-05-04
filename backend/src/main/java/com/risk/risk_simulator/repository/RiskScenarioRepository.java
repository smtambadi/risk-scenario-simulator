package com.risk.risk_simulator.repository;

import com.risk.risk_simulator.entity.RiskScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskScenarioRepository extends JpaRepository<RiskScenario, Integer> {

    List<RiskScenario> findByStatus(String status);

    List<RiskScenario> findByCategory(String category);

    List<RiskScenario> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT r FROM RiskScenario r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<RiskScenario> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);
}
