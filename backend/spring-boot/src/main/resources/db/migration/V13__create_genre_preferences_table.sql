-- V13: Create genre_preferences table for user taste onboarding and tracking
-- Stores both explicit (user-selected) and implicit (calculated) genre preferences

CREATE TABLE genre_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    genre VARCHAR(100) NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    preference_score INTEGER NOT NULL DEFAULT 5,
    explicit BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Foreign key
    CONSTRAINT fk_genre_pref_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Unique constraint: One preference per user per genre per media type
    CONSTRAINT uk_user_genre_media UNIQUE(user_id, genre, media_type),
    
    -- Check constraints
    CONSTRAINT chk_genre_pref_score CHECK (preference_score >= 1 AND preference_score <= 10),
    CONSTRAINT chk_genre_pref_media_type CHECK (media_type IN ('MOVIE', 'BOOK', 'GAME'))
);

-- Indexes for performance
CREATE INDEX idx_genre_pref_user ON genre_preferences(user_id);
CREATE INDEX idx_genre_pref_media_type ON genre_preferences(media_type);
CREATE INDEX idx_genre_pref_score ON genre_preferences(preference_score DESC);
CREATE INDEX idx_genre_pref_explicit ON genre_preferences(explicit);
CREATE INDEX idx_genre_pref_genre ON genre_preferences(genre);

-- Comments
COMMENT ON TABLE genre_preferences IS 'User genre preferences with 1-10 scoring system';
COMMENT ON COLUMN genre_preferences.user_id IS 'Reference to the user';
COMMENT ON COLUMN genre_preferences.genre IS 'Genre name (e.g., Action, Drama, Sci-Fi)';
COMMENT ON COLUMN genre_preferences.media_type IS 'Media type: MOVIE, BOOK, or GAME';
COMMENT ON COLUMN genre_preferences.preference_score IS 'Score from 1 (dislike) to 10 (love)';
COMMENT ON COLUMN genre_preferences.explicit IS 'true = user-selected, false = calculated from ratings';
COMMENT ON COLUMN genre_preferences.updated_at IS 'Last modified timestamp (for decay calculation)';
