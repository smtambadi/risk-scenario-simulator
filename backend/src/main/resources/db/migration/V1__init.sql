CREATE TABLE risk_scenarios (
    id SERIAL PRIMARY KEY,

    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,

    category VARCHAR(100),
    risk_level VARCHAR(50),

    status VARCHAR(50) DEFAULT 'ACTIVE',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_risk_category ON risk_scenarios(category);
CREATE INDEX idx_risk_status ON risk_scenarios(status);
CREATE INDEX idx_risk_created_at ON risk_scenarios(created_at);