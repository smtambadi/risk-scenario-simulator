package com.risk.risk_simulator.exception;

/**
 * Exception thrown when Risk Scenario data is invalid or violates business rules
 */
public class InvalidRiskScenarioException extends RiskScenarioException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidRiskScenarioException(String message) {
        super(message);
    }
    
    public InvalidRiskScenarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
