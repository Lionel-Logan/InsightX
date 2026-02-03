package com.insightx.services;

// WatchedService - Manages user's watched/consumed media tracking
//
// Responsibilities:
// - Mark media as watched
// - Remove watched entry (un-watch)
// - Get watch history
// - Check if media is watched
// - Get watch statistics
//
// Key Methods:
//
// markAsWatched(UUID userId, MarkWatchedRequest request): WatchedEntryDTO
// - Validate media ID and type
// - Check if already watched (handle duplicate gracefully)
// - Create new WatchedEntry
// - Save to database
// - Trigger taste profile recalculation (async)
// - Return watched entry DTO
//
// removeWatched(UUID userId, String mediaId, MediaType mediaType): void
// - Find watched entry
// - Delete from database
// - Optional: Trigger taste profile recalculation
//
// getWatchHistory(UUID userId, MediaType mediaType, Pageable pageable): Page<WatchedEntryDTO>
// - Fetch watched entries (filtered by type if specified)
// - Order by watchedDate descending
// - Return paginated results
//
// isWatched(UUID userId, String mediaId, MediaType mediaType): boolean
// - Quick check if media is watched
// - Use repository exists() method for efficiency
//
// getWatchStatistics(UUID userId): WatchStatsDTO
// - Total watched count
// - Count by media type
// - Recent activity (last 30 days)
// - Most active month
// - Return aggregated stats
//
// getRecentlyWatched(UUID userId, int days): List<WatchedEntryDTO>
// - Get items watched in last N days
// - Useful for "Continue watching" or activity feed
//
// Dependencies:
// - WatchedEntryRepository
// - TasteProfileService (for recalculation trigger)
//
// Business Rules:
// - User can only mark media as watched once
// - If already watched, update watchedDate (optional)
// - watchedDate defaults to current date if not provided
// - Un-watching should be allowed
//
// Integration:
// - When marking as watched, optionally fetch media details from FastAPI
// - Store only user-media relationship, not full media data
//
// Transaction Management:
// - @Transactional for write operations
// - Read-only for queries
//
// Error Handling:
// - MediaAlreadyWatchedException (handle gracefully)
// - InvalidMediaException
// - UserNotFoundException