-- Create audit_log table for tracking entity changes
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id INTEGER NOT NULL,
    action VARCHAR(20) NOT NULL,
    changed_fields TEXT,
    old_values TEXT,
    new_values TEXT,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Composite index on entity_type and entity_id for efficient lookups
CREATE INDEX idx_audit_entity_type_id ON audit_log(entity_type, entity_id);

-- Indexes on frequently queried columns
CREATE INDEX idx_audit_action ON audit_log(action);
CREATE INDEX idx_audit_changed_at ON audit_log(changed_at);
CREATE INDEX idx_audit_entity_type ON audit_log(entity_type);
CREATE INDEX idx_audit_entity_id ON audit_log(entity_id);

-- Composite index for common query: find changes for specific entity in date range
CREATE INDEX idx_audit_entity_date ON audit_log(entity_type, entity_id, changed_at);
