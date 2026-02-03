-- V3: Create user_sessions table
-- JWT token management for logout and session revocation

CREATE TABLE user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_session_user ON user_sessions(user_id);
CREATE INDEX idx_session_token ON user_sessions(token_hash);
CREATE INDEX idx_session_expires ON user_sessions(expires_at);

-- Comments for documentation
COMMENT ON TABLE user_sessions IS 'Active user sessions and JWT tokens for logout functionality';
COMMENT ON COLUMN user_sessions.token_hash IS 'SHA-256 hash of JWT token';
COMMENT ON COLUMN user_sessions.device_info IS 'User agent or device description';
COMMENT ON COLUMN user_sessions.ip_address IS 'IP address of session (IPv4 or IPv6)';
COMMENT ON COLUMN user_sessions.revoked IS 'True if token has been manually revoked';
