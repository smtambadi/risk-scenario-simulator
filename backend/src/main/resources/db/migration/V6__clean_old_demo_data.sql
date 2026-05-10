-- V6: Clear old demo data and reset for fresh seed
-- Remove old records that use deprecated 'ACTIVE' status
DELETE FROM risk_scenarios WHERE status = 'ACTIVE';
DELETE FROM users WHERE username IN ('admin', 'manager', 'viewer');
