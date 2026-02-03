package com.insightx.services;

// PreferenceService - Manages user preferences and settings
//
// Responsibilities:
// - Get user preferences
// - Update preferences
// - Delete specific preference
// - Reset all preferences
// - Get preference by key
//
// Key Methods:
//
// getAllPreferences(UUID userId): Map<String, String>
// - Get all preferences for user
// - Return as key-value map
// - Cache in Redis for performance
//
// getPreference(UUID userId, String key): Optional<String>
// - Get single preference value
// - Return Optional.empty() if not set
//
// setPreference(UUID userId, String key, String value): void
// - Create or update preference
// - Validate key and value formats
// - Save to database
// - Update cache
//
// setMultiplePreferences(UUID userId, Map<String, String> preferences): void
// - Batch update multiple preferences
// - Use transaction for atomicity
// - Update cache
//
// deletePreference(UUID userId, String key): void
// - Remove specific preference
// - Invalidate cache
//
// resetPreferences(UUID userId): void
// - Delete all preferences for user
// - Reset to default values
// - Clear cache
//
// getPreferenceOrDefault(UUID userId, String key, String defaultValue): String
// - Get preference value or return default
// - Convenience method
//
// Dependencies:
// - UserPreferenceRepository
// - RedisTemplate (for caching)
//
// Common Preference Keys (for validation):
// - "theme": ["dark", "light"]
// - "language": ["en", "es", "fr", "de", "ja", "hi"]
// - "explicit_content": ["true", "false"]
// - "notification_recommendations": ["true", "false"]
// - "default_media_type": ["movie", "book", "game", "all"]
// - "results_per_page": ["20", "50", "100"]
// - "auto_play_trailers": ["true", "false"]
//
// Validation:
// - Key format: lowercase, alphanumeric + underscore
// - Value: depends on key (use validator pattern)
// - Max value length: 1000 chars
//
// Cache Strategy:
// - Cache all user preferences under key: "user:prefs:{userId}"
// - TTL: 24 hours (preferences change infrequently)
// - Invalidate on any preference change
// - Load from DB on cache miss
//
// Transaction Management:
// - @Transactional for batch updates
// - Ensure atomicity
//
// Error Handling:
// - InvalidPreferenceKeyException
// - InvalidPreferenceValueException
// - UserNotFoundException
//
// Usage Pattern:
// - Load all preferences at user login
// - Store in client-side cache (mobile/web)
// - Sync changes back to server
// - Preferences affect UI behavior and recommendations
//
// Future Enhancements:
// - Preference templates (default sets for new users)
// - Preference versioning (schema evolution)
// - Preference sync across devices
// - Admin-defined preference options