package com.insightx.repositories;

// WatchedEntryRepository - Data access for watched media tracking
// Extends JpaRepository for CRUD operations
//
// Custom Query Methods:
// - findByUserId(UUID userId): List<WatchedEntry>
//   Get all watched entries for a user
//
// - findByUserIdAndMediaType(UUID userId, MediaType mediaType): List<WatchedEntry>
//   Get watched entries filtered by media type
//
// - findByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): Optional<WatchedEntry>
//   Check if user has watched specific media
//
// - existsByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): boolean
//   Quick check if media is watched (more efficient than findBy)
//
// - findByUserIdOrderByWatchedDateDesc(UUID userId): List<WatchedEntry>
//   Get watch history in reverse chronological order
//
// - findByUserIdAndWatchedDateAfter(UUID userId, LocalDate date): List<WatchedEntry>
//   Get recently watched items (e.g., last 30 days)
//
// - countByUserId(UUID userId): long
//   Count total watched items for user
//
// - countByUserIdAndMediaType(UUID userId, MediaType mediaType): long
//   Count watched items by type
//
// - deleteByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): void
//   Remove watched entry (un-watch feature)
//
// Pagination Support:
// - Page<WatchedEntry> findByUserId(UUID userId, Pageable pageable)
//   For paginated watch history
//
// Custom Queries (if needed):
// - @Query for complex aggregations
// - Native queries for PostgreSQL-specific features
//
// Performance Tips:
// - Index on (userId, watchedDate) for chronological queries
// - Use exists() instead of find() when checking presence
// - Consider batch operations for bulk imports