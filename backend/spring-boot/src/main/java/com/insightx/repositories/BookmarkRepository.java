package com.insightx.repositories;

// BookmarkRepository - Data access for user bookmarks
// Extends JpaRepository for CRUD operations
//
// Custom Query Methods:
// - findByUserId(UUID userId): List<Bookmark>
//   Get all bookmarks for user
//
// - findByUserIdOrderBySavedAtDesc(UUID userId): List<Bookmark>
//   Get bookmarks in reverse chronological order (most recent first)
//
// - findByUserIdAndMediaType(UUID userId, MediaType mediaType): List<Bookmark>
//   Get bookmarks filtered by media type
//
// - findByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): Optional<Bookmark>
//   Get specific bookmark
//
// - existsByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): boolean
//   Check if media is bookmarked by user
//
// - countByUserId(UUID userId): long
//   Count total bookmarks for user
//
// - countByUserIdAndMediaType(UUID userId, MediaType mediaType): long
//   Count bookmarks by media type
//
// - deleteByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): void
//   Remove bookmark
//
// - findByUserIdAndSavedAtAfter(UUID userId, LocalDateTime date): List<Bookmark>
//   Get recently bookmarked items
//
// Pagination Support:
// - Page<Bookmark> findByUserIdOrderBySavedAtDesc(UUID userId, Pageable pageable)
//   For paginated bookmark list
//
// Bulk Operations:
// - @Modifying
//   @Query("DELETE FROM Bookmark b WHERE b.userId = :userId")
//   void deleteAllByUserId(@Param("userId") UUID userId)
//   Clear all bookmarks for user
//
// Statistics Queries:
// - @Query("SELECT b.mediaType, COUNT(b) FROM Bookmark b WHERE b.userId = :userId GROUP BY b.mediaType")
//   List<Object[]> getBookmarkCountByMediaType(@Param("userId") UUID userId)
//   Get distribution of bookmarks
//
// Performance Tips:
// - Index on (userId, savedAt) for chronological queries
// - Index on (userId, mediaType) for filtered queries
// - Use exists() for bookmark state checks
// - Batch operations for importing bookmarks
//
// UI Integration:
// - Quick lookup for showing bookmark icon state
// - Efficient pagination for long bookmark lists
// - Filter by media type for segmented views