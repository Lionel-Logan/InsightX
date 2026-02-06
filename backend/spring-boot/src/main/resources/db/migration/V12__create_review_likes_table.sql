-- V12: Create review_likes table for Instagram-style upvote system
-- Users can like reviews, undo likes, and see upvote counts

CREATE TABLE review_likes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_id UUID NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Foreign keys
    CONSTRAINT fk_review_like_review FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_like_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Unique constraint: One like per user per review
    CONSTRAINT uk_review_user_like UNIQUE(review_id, user_id)
);

-- Indexes for performance
CREATE INDEX idx_review_likes_review ON review_likes(review_id);
CREATE INDEX idx_review_likes_user ON review_likes(user_id);
CREATE INDEX idx_review_likes_created ON review_likes(created_at DESC);

-- Comments
COMMENT ON TABLE review_likes IS 'Tracks user likes/upvotes on reviews (Instagram-style)';
COMMENT ON COLUMN review_likes.review_id IS 'Reference to the review being liked';
COMMENT ON COLUMN review_likes.user_id IS 'User who liked the review';
COMMENT ON COLUMN review_likes.created_at IS 'When the like was created';
