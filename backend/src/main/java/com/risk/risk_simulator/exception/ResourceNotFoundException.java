package com.risk.risk_simulator.exception;

/**
 * Exception thrown when a requested Risk Scenario resource is not found
 */
public class ResourceNotFoundException extends RiskScenarioException {
    
    private static final long serialVersionUID = 1L;
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(Integer id) {
        super("Risk Scenario not found with id: " + id);
    }
}
