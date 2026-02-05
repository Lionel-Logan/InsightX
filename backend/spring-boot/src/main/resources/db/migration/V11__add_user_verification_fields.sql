-- Migration: Add email verification and authentication fields to users table
-- Description: Adds email_verified, verification_token, verification_token_expiry,
--              verification_attempts, last_login_at, and role columns to support
--              Phase 3 Security Layer Implementation

-- Add email verification fields
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS verification_token VARCHAR(6),
    ADD COLUMN IF NOT EXISTS verification_token_expiry TIMESTAMP,
    ADD COLUMN IF NOT EXISTS verification_attempts INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Create index on email_verified for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified);

-- Create index on role for future RBAC queries
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Create index on last_login_at for analytics
CREATE INDEX IF NOT EXISTS idx_users_last_login ON users(last_login_at);

-- Add comment to table
COMMENT ON COLUMN users.email_verified IS 'Whether user has verified their email address';
COMMENT ON COLUMN users.verification_token IS '6-digit verification code sent to email';
COMMENT ON COLUMN users.verification_token_expiry IS 'Expiration timestamp for verification code (24 hours)';
COMMENT ON COLUMN users.verification_attempts IS 'Number of failed verification attempts (max 5)';
COMMENT ON COLUMN users.last_login_at IS 'Timestamp of user''s last successful login';
COMMENT ON COLUMN users.role IS 'User role for RBAC (USER, ADMIN, etc.)';
