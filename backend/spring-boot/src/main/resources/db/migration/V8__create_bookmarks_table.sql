-- V8: Create bookmarks table
-- User's saved/bookmarked media for later consumption

CREATE TABLE bookmarks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    media_id VARCHAR(100) NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    notes VARCHAR(500),
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_media_bookmark UNIQUE (user_id, media_id, media_type),
    CONSTRAINT chk_media_type CHECK (media_type IN ('MOVIE', 'BOOK', 'GAME')),
    CONSTRAINT chk_visibility CHECK (visibility IN ('PUBLIC', 'FOLLOWERS_ONLY', 'PRIVATE'))
);

-- Indexes for performance
CREATE INDEX idx_bookmark_user ON bookmarks(user_id);
CREATE INDEX idx_bookmark_saved ON bookmarks(saved_at);
CREATE INDEX idx_bookmark_user_saved ON bookmarks(user_id, saved_at DESC);

-- Comments for documentation
COMMENT ON TABLE bookmarks IS 'User watchlists/reading lists - media saved for later';
COMMENT ON COLUMN bookmarks.notes IS 'Optional user notes about why they saved this';
COMMENT ON COLUMN bookmarks.visibility IS 'Who can see this bookmark: PUBLIC, FOLLOWERS_ONLY, or PRIVATE';
