-- V7: Ensure clean demo user state
-- Remove any test users that may have been created during development
DELETE FROM users WHERE username NOT IN ('admin', 'manager', 'viewer');
-- Update any existing demo user to correct roles
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
UPDATE users SET role = 'MANAGER' WHERE username = 'manager';
UPDATE users SET role = 'VIEWER' WHERE username = 'viewer';
