-- V4__add_soft_delete_to_risk_scenarios.sql
-- Add soft delete support to risk_scenarios table

ALTER TABLE risk_scenarios
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Create index for soft delete queries
CREATE INDEX idx_risk_is_deleted ON risk_scenarios(is_deleted);

-- Composite index for common queries
CREATE INDEX idx_risk_active_created ON risk_scenarios(is_deleted, created_at DESC);
