package com.insightx.repositories;

// UserPreferenceRepository - Data access for user preferences
// Extends JpaRepository for CRUD operations
//
// Custom Query Methods:
// - findByUserId(UUID userId): List<UserPreference>
//   Get all preferences for user (typically loaded at login)
//
// - findByUserIdAndKey(UUID userId, String key): Optional<UserPreference>
//   Get specific preference by key
//
// - existsByUserIdAndKey(UUID userId, String key): boolean
//   Check if preference exists
//
// - deleteByUserIdAndKey(UUID userId, String key): void
//   Remove specific preference
//
// Bulk Operations:
// - @Modifying
//   @Query("DELETE FROM UserPreference up WHERE up.userId = :userId")
//   void deleteAllByUserId(@Param("userId") UUID userId)
//   Reset all preferences for user
//
// Batch Retrieval:
// - @Query("SELECT up FROM UserPreference up WHERE up.userId = :userId AND up.key IN :keys")
//   List<UserPreference> findByUserIdAndKeyIn(@Param("userId") UUID userId, @Param("keys") List<String> keys)
//   Get multiple preferences at once
//
// Common Preference Keys (for reference):
// - "theme" -> "dark" | "light"
// - "language" -> "en" | "es" | "fr" | etc.
// - "explicit_content" -> "true" | "false"
// - "notification_recommendations" -> "true" | "false"
// - "default_media_type" -> "movie" | "book" | "game"
// - "results_per_page" -> "20" | "50" | "100"
// - "auto_play_trailers" -> "true" | "false"
//
// Usage Pattern:
// - Load all preferences at user login
// - Cache in application memory or Redis
// - Update individual preferences via PUT requests
// - Return as Map<String, String> to service layer
//
// Performance Tips:
// - Index on (userId, key) for fast lookups
// - Fetch all preferences for user in one query
// - Cache frequently accessed preferences
// - Consider using @Cacheable annotation
//
// Transaction Management:
// - Use @Transactional for batch updates
// - Ensure atomic updates for related preferences