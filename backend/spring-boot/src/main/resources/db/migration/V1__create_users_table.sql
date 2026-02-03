-- V1: Create users table
-- Core user accounts with authentication and profile information

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    region VARCHAR(10) DEFAULT 'US',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_region ON users(region);
CREATE INDEX idx_created_at ON users(created_at);

-- Comments for documentation
COMMENT ON TABLE users IS 'User accounts with authentication and profile data';
COMMENT ON COLUMN users.id IS 'Primary key - UUID';
COMMENT ON COLUMN users.username IS 'Unique username (3-50 chars)';
COMMENT ON COLUMN users.email IS 'Unique email address';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN users.region IS 'ISO country code (e.g., US, IN, GB)';
COMMENT ON COLUMN users.active IS 'Soft delete flag - false means account is deactivated';
