-- V10: Additional indexes for performance optimization
-- Composite and specialized indexes for common query patterns

-- Social features - find followed users' recent activity
CREATE INDEX idx_watched_entries_user_type_date ON watched_entries(user_id, media_type, watched_date DESC);
CREATE INDEX idx_ratings_user_type_created ON ratings(user_id, media_type, created_at DESC);

-- Discovery - find high-rated items
CREATE INDEX idx_ratings_high_rating ON ratings(rating) WHERE rating >= 8;

-- Analytics - user activity tracking
CREATE INDEX idx_reviews_user_created ON reviews(user_id, created_at DESC);
CREATE INDEX idx_bookmarks_media_type ON bookmarks(media_type);

-- Session management - cleanup expired sessions
CREATE INDEX idx_sessions_expired ON user_sessions(expires_at, revoked) WHERE revoked = false;

-- User statistics - count queries
CREATE INDEX idx_user_follows_counts ON user_follows(follower_id, following_id);

-- Comments for documentation
COMMENT ON INDEX idx_watched_entries_user_type_date IS 'Optimize filtered watch history queries';
COMMENT ON INDEX idx_ratings_high_rating IS 'Partial index for high-rated content discovery';
COMMENT ON INDEX idx_sessions_expired IS 'Partial index for expired session cleanup';
