-- V7: Create reviews table
-- User written reviews for media

CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    media_id VARCHAR(100) NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    review_text VARCHAR(5000) NOT NULL,
    is_spoiler BOOLEAN NOT NULL DEFAULT false,
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_media_review UNIQUE (user_id, media_id, media_type),
    CONSTRAINT chk_media_type CHECK (media_type IN ('MOVIE', 'BOOK', 'GAME')),
    CONSTRAINT chk_visibility CHECK (visibility IN ('PUBLIC', 'FOLLOWERS_ONLY', 'PRIVATE')),
    CONSTRAINT chk_review_length CHECK (LENGTH(review_text) BETWEEN 10 AND 5000)
);

-- Indexes for performance
CREATE INDEX idx_review_user ON reviews(user_id);
CREATE INDEX idx_review_media ON reviews(media_id);
CREATE INDEX idx_review_created ON reviews(created_at);

-- Comments for documentation
COMMENT ON TABLE reviews IS 'User written reviews and detailed opinions on media';
COMMENT ON COLUMN reviews.review_text IS 'Review content (10-5000 characters)';
COMMENT ON COLUMN reviews.is_spoiler IS 'True if review contains spoilers';
COMMENT ON COLUMN reviews.visibility IS 'Who can see this review: PUBLIC, FOLLOWERS_ONLY, or PRIVATE';
