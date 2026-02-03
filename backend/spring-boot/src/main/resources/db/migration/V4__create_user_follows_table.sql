-- V4: Create user_follows table
-- Social networking - follow relationships between users

CREATE TABLE user_follows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    following_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_follower_following UNIQUE (follower_id, following_id),
    CONSTRAINT chk_no_self_follow CHECK (follower_id != following_id)
);

-- Indexes for performance
CREATE INDEX idx_follow_follower ON user_follows(follower_id);
CREATE INDEX idx_follow_following ON user_follows(following_id);
CREATE INDEX idx_follow_created ON user_follows(created_at);

-- Comments for documentation
COMMENT ON TABLE user_follows IS 'Twitter-style follow relationships - A follows B (unidirectional)';
COMMENT ON COLUMN user_follows.follower_id IS 'User who is following';
COMMENT ON COLUMN user_follows.following_id IS 'User being followed';
COMMENT ON CONSTRAINT chk_no_self_follow ON user_follows IS 'Prevent users from following themselves';
