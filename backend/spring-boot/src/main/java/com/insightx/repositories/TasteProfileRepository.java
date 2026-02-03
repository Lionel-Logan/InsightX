package com.insightx.repositories;

// TasteProfileRepository - Data access for taste profiles
// Extends JpaRepository for CRUD operations
//
// Custom Query Methods:
// - findByUserId(UUID userId): Optional<TasteProfile>
//   Get taste profile for user (one-to-one relationship)
//
// - existsByUserId(UUID userId): boolean
//   Check if profile exists for user
//
// - findByLastCalculatedBefore(LocalDateTime date): List<TasteProfile>
//   Find stale profiles needing recalculation
//   Example: Find profiles older than 7 days
//
// - deleteByUserId(UUID userId): void
//   Remove profile (will be regenerated on next request)
//
// Recalculation Queries:
// - @Query("SELECT tp FROM TasteProfile tp WHERE tp.lastCalculated < :threshold OR tp.userId IN :userIds")
//   List<TasteProfile> findStaleOrSpecificProfiles(@Param("threshold") LocalDateTime threshold, @Param("userIds") List<UUID> userIds)
//   Find profiles to recalculate (either old or specific users)
//
// Batch Operations:
// - @Modifying
//   @Query("UPDATE TasteProfile tp SET tp.lastCalculated = :date WHERE tp.userId IN :userIds")
//   void updateLastCalculatedForUsers(@Param("userIds") List<UUID> userIds, @Param("date") LocalDateTime date)
//   Mark profiles as recalculated
//
// Profile Data Access:
// - Store profileData as JSON/JSONB in PostgreSQL
// - Use @Type(JsonType.class) or similar for JSON mapping
// - Query specific JSON fields using native queries if needed
//
// Example JSON Queries (PostgreSQL specific):
// - @Query(value = "SELECT * FROM taste_profiles WHERE profile_data->>'averageRating' > :minRating", nativeQuery = true)
//   List<TasteProfile> findByMinAverageRating(@Param("minRating") String minRating)
//
// Cache Strategy:
// - Always check Redis cache first (service layer)
// - Cache key: "taste_profile:{userId}"
// - TTL: 1 hour
// - Invalidate on recalculation
//
// Recalculation Triggers (handled in service layer):
// - New rating added (async)
// - Profile older than 7 days
// - User requests refresh
// - After every 5 new ratings
//
// Performance Tips:
// - Index on userId (unique)
// - Index on lastCalculated for finding stale profiles
// - Use JSONB type in PostgreSQL for better performance
// - Consider GIN index on profileData for JSON queries
//
// Future Enhancements:
// - Store profile versions for schema evolution
// - Keep historical profiles for tracking changes
// - Similarity matching between profiles