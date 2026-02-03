-- V2: Create user_preferences table
-- Key-value storage for user preferences and privacy settings

CREATE TABLE user_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    preference_key VARCHAR(100) NOT NULL,
    preference_value VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_key UNIQUE (user_id, preference_key)
);

-- Indexes for performance
CREATE INDEX idx_user_preference_user ON user_preferences(user_id);

-- Comments for documentation
COMMENT ON TABLE user_preferences IS 'Flexible key-value storage for user settings and privacy controls';
COMMENT ON COLUMN user_preferences.preference_key IS 'Preference key (e.g., privacy.profile, theme, language)';
COMMENT ON COLUMN user_preferences.preference_value IS 'Preference value (can be JSON for complex objects)';

-- Common preference keys:
-- privacy.profile: public | followers | private
-- privacy.watch_history: true | false
-- privacy.ratings: true | false
-- privacy.reviews: public | followers | private
-- privacy.bookmarks: true | false
-- theme: dark | light
-- language: en | es | fr
