-- V5: Create watched_entries table
-- Track user's watched/read/played media consumption

CREATE TABLE watched_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    media_id VARCHAR(100) NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    watched_date DATE NOT NULL DEFAULT CURRENT_DATE,
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_media UNIQUE (user_id, media_id, media_type),
    CONSTRAINT chk_media_type CHECK (media_type IN ('MOVIE', 'BOOK', 'GAME')),
    CONSTRAINT chk_visibility CHECK (visibility IN ('PUBLIC', 'FOLLOWERS_ONLY', 'PRIVATE'))
);

-- Indexes for performance
CREATE INDEX idx_watched_user ON watched_entries(user_id);
CREATE INDEX idx_watched_date ON watched_entries(watched_date);
CREATE INDEX idx_watched_user_date ON watched_entries(user_id, watched_date DESC);

-- Comments for documentation
COMMENT ON TABLE watched_entries IS 'User consumption history - movies watched, books read, games played';
COMMENT ON COLUMN watched_entries.media_id IS 'External media ID from TMDB/books/games API';
COMMENT ON COLUMN watched_entries.media_type IS 'Type of media: MOVIE, BOOK, or GAME';
COMMENT ON COLUMN watched_entries.watched_date IS 'Date user marked media as consumed';
COMMENT ON COLUMN watched_entries.visibility IS 'Who can see this entry: PUBLIC, FOLLOWERS_ONLY, or PRIVATE';
