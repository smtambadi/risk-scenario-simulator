-- V5: Add missing columns to risk_scenarios for full feature support
ALTER TABLE risk_scenarios ADD COLUMN IF NOT EXISTS risk_score DOUBLE PRECISION DEFAULT 0.0;
ALTER TABLE risk_scenarios ADD COLUMN IF NOT EXISTS impact VARCHAR(50) DEFAULT 'MEDIUM';
ALTER TABLE risk_scenarios ADD COLUMN IF NOT EXISTS likelihood VARCHAR(50) DEFAULT 'MEDIUM';
ALTER TABLE risk_scenarios ADD COLUMN IF NOT EXISTS mitigation_plan TEXT;
ALTER TABLE risk_scenarios ADD COLUMN IF NOT EXISTS projected_cost BIGINT DEFAULT 0;
ALTER TABLE risk_scenarios ADD COLUMN IF NOT EXISTS ai_description TEXT;
ALTER TABLE risk_scenarios ADD COLUMN IF NOT EXISTS ai_recommendations TEXT;

-- Add indexes for common queries
CREATE INDEX IF NOT EXISTS idx_risk_scenarios_impact ON risk_scenarios(impact);
CREATE INDEX IF NOT EXISTS idx_risk_scenarios_likelihood ON risk_scenarios(likelihood);
CREATE INDEX IF NOT EXISTS idx_risk_scenarios_risk_score ON risk_scenarios(risk_score);
