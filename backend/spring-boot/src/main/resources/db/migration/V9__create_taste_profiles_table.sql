-- V9: Create taste_profiles table
-- Computed user taste preferences cached as JSONB

CREATE TABLE taste_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    profile_data JSONB,
    version INTEGER NOT NULL DEFAULT 1,
    last_calculated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_taste_profile_user ON taste_profiles(user_id);
CREATE INDEX idx_taste_profile_calculated ON taste_profiles(last_calculated);

-- GIN index for JSONB queries (optional, for advanced querying)
CREATE INDEX idx_taste_profile_data_gin ON taste_profiles USING gin(profile_data);

-- Comments for documentation
COMMENT ON TABLE taste_profiles IS 'Computed user taste profiles for recommendation engine';
COMMENT ON COLUMN taste_profiles.profile_data IS 'JSONB containing genre preferences, themes, behavior patterns';
COMMENT ON COLUMN taste_profiles.version IS 'Profile schema version for evolution';
COMMENT ON COLUMN taste_profiles.last_calculated IS 'When profile was last computed';

-- Example profile_data structure:
-- {
--   "genrePreferences": {"Action": 0.85, "Drama": 0.65, "Sci-Fi": 0.90},
--   "themeAffinities": {"time-travel": 0.75, "redemption": 0.60},
--   "averageRating": 7.5,
--   "totalRatings": 45,
--   "favoriteCreators": ["Christopher Nolan", "Denis Villeneuve"],
--   "mediaTypeDistribution": {"movie": 60, "book": 25, "game": 15}
-- }
