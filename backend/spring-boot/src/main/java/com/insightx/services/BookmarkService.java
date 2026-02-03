package com.insightx.services;

// BookmarkService - Manages user's saved/bookmarked media
//
// Responsibilities:
// - Add bookmark
// - Remove bookmark
// - Get user's bookmarks
// - Check bookmark status
// - Get bookmark statistics
//
// Key Methods:
//
// addBookmark(UUID userId, BookmarkRequest request): BookmarkDTO
// - Validate media ID and type
// - Check if already bookmarked (handle gracefully)
// - Create new Bookmark entity
// - Set savedAt to current timestamp
// - Save to database
// - Return bookmark DTO
//
// removeBookmark(UUID userId, String mediaId, MediaType mediaType): void
// - Find bookmark
// - Delete from database
//
// getUserBookmarks(UUID userId, MediaType mediaType, Pageable pageable): Page<BookmarkDTO>
// - Get all bookmarks for user
// - Optional filter by media type
// - Order by savedAt descending (most recent first)
// - Return paginated results
//
// isBookmarked(UUID userId, String mediaId, MediaType mediaType): boolean
// - Quick check if media is bookmarked
// - Use repository exists() for efficiency
//
// getBookmarkStatistics(UUID userId): BookmarkStatsDTO
// - Total bookmark count
// - Count by media type
// - Recently bookmarked (last 7 days)
// - Return aggregated stats
//
// updateBookmarkNotes(UUID userId, String mediaId, MediaType mediaType, String notes): BookmarkDTO
// - Find bookmark
// - Update notes field
// - Save changes
// - Return updated bookmark
//
// clearAllBookmarks(UUID userId, MediaType mediaType): void
// - Delete all bookmarks for user
// - Optional: Filter by media type
// - Use for bulk operations
//
// Dependencies:
// - BookmarkRepository
// - Optional: FastAPI service to enrich bookmark data with media details
//
// Business Rules:
// - User can bookmark media multiple times (just update savedAt)
//   OR enforce unique constraint (current design)
// - Notes are optional (max 500 chars)
// - Bookmarks independent of watched/rated status
//
// UI Integration:
// - Show bookmark icon state in media cards
// - Toggle bookmark with single tap/click
// - Display bookmark count in user profile
// - Separate views for movies/books/games bookmarks
//
// API Response Enrichment:
// - When returning bookmarks, optionally include:
//   * Media title, poster, rating from FastAPI/cache
//   * Watched status
//   * User's rating
// - This requires calling FastAPI or checking other tables
//
// Transaction Management:
// - @Transactional for write operations
//
// Error Handling:
// - BookmarkAlreadyExistsException (handle gracefully)
// - BookmarkNotFoundException
// - ValidationException
//
// Performance:
// - Cache bookmark existence checks in Redis
// - Invalidate cache on bookmark changes
// - Use batch queries for multiple bookmark checks
//
// Future Enhancements:
// - Bookmark collections/folders
// - Share bookmarks with other users
// - Export/import bookmarks
// - Recommended bookmarks based on other users