package com.risk.risk_simulator.exception;

/**
 * Base exception for all Risk Scenario related errors
 */
public class RiskScenarioException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public RiskScenarioException(String message) {
        super(message);
    }
    
    public RiskScenarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
