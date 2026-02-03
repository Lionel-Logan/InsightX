-- V6: Create ratings table
-- User ratings for media content (1-10 scale)

CREATE TABLE ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    media_id VARCHAR(100) NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    rating INTEGER NOT NULL,
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_media_rating UNIQUE (user_id, media_id, media_type),
    CONSTRAINT chk_rating_value CHECK (rating BETWEEN 1 AND 10),
    CONSTRAINT chk_media_type CHECK (media_type IN ('MOVIE', 'BOOK', 'GAME')),
    CONSTRAINT chk_visibility CHECK (visibility IN ('PUBLIC', 'FOLLOWERS_ONLY', 'PRIVATE'))
);

-- Indexes for performance
CREATE INDEX idx_rating_user ON ratings(user_id);
CREATE INDEX idx_rating_created ON ratings(created_at);
CREATE INDEX idx_rating_value ON ratings(rating);

-- Comments for documentation
COMMENT ON TABLE ratings IS 'User ratings for media - powers recommendation engine';
COMMENT ON COLUMN ratings.rating IS 'Rating value (1-10 scale)';
COMMENT ON COLUMN ratings.visibility IS 'Who can see this rating: PUBLIC, FOLLOWERS_ONLY, or PRIVATE';
COMMENT ON CONSTRAINT chk_rating_value ON ratings IS 'Rating must be between 1 and 10 inclusive';
