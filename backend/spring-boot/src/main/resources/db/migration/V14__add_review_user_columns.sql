-- V14: Add denormalized upvote count to reviews and avatar URL to users

-- Add upvote_count to reviews table for performance
ALTER TABLE reviews ADD COLUMN upvote_count INTEGER NOT NULL DEFAULT 0;

-- Index for sorting reviews by popularity
CREATE INDEX idx_reviews_upvotes ON reviews(upvote_count DESC);
CREATE INDEX idx_reviews_media_upvotes ON reviews(media_id, media_type, upvote_count DESC);

-- Add avatar_url to users for profile display in reviews
ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500);

-- Comments
COMMENT ON COLUMN reviews.upvote_count IS 'Denormalized count of likes for performance (managed by application)';
COMMENT ON COLUMN users.avatar_url IS 'User profile avatar image URL';
