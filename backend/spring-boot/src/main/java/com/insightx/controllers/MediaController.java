package com.insightx.controllers;

// MediaController - REST API endpoints for media operations
// Base path: /api/media
// All endpoints require authentication (JWT)
//
// Endpoints:
//
// GET /api/media/{mediaType}/{mediaId}
// - Get complete media metadata
// - Path variables: mediaType (movie|book|game), mediaId
// - Response: MediaMetadataDTO (from FastAPI)
// - Status: 200 OK
// - Errors: 404 if media not found
//
// POST /api/media/watched
// - Mark media as watched/consumed
// - Request body: MarkWatchedRequest (mediaId, mediaType, watchedDate)
// - Response: WatchedEntryDTO
// - Status: 201 Created
// - Note: Also triggers taste profile update
//
// DELETE /api/media/watched/{mediaType}/{mediaId}
// - Remove watched status
// - Path variables: mediaType, mediaId
// - Response: success message
// - Status: 200 OK
//
// GET /api/media/watched
// - Get user's watch history
// - Query params: mediaType (optional), page, size, sort
// - Response: Page<WatchedEntryDTO>
// - Status: 200 OK
//
// GET /api/media/watched/check/{mediaType}/{mediaId}
// - Check if media is watched by user
// - Path variables: mediaType, mediaId
// - Response: { "watched": true/false }
// - Status: 200 OK
//
// POST /api/media/rating
// - Submit or update rating for media
// - Request body: RatingRequest (mediaId, mediaType, rating: 1-10)
// - Response: RatingDTO
// - Status: 201 Created or 200 OK if updating
// - Note: Triggers taste profile update
//
// DELETE /api/media/rating/{mediaType}/{mediaId}
// - Delete rating
// - Path variables: mediaType, mediaId
// - Response: success message
// - Status: 200 OK
//
// GET /api/media/ratings
// - Get user's ratings
// - Query params: mediaType (optional), page, size, sort
// - Response: Page<RatingDTO>
// - Status: 200 OK
//
// GET /api/media/rating/{mediaType}/{mediaId}
// - Get user's rating for specific media
// - Path variables: mediaType, mediaId
// - Response: RatingDTO or 404
// - Status: 200 OK
//
// POST /api/media/bookmark
// - Add media to bookmarks
// - Request body: BookmarkRequest (mediaId, mediaType, notes)
// - Response: BookmarkDTO
// - Status: 201 Created
//
// DELETE /api/media/bookmark/{mediaType}/{mediaId}
// - Remove bookmark
// - Path variables: mediaType, mediaId
// - Response: success message
// - Status: 200 OK
//
// GET /api/media/bookmarks
// - Get user's bookmarks
// - Query params: mediaType (optional), page, size, sort
// - Response: Page<BookmarkDTO>
// - Status: 200 OK
//
// GET /api/media/bookmark/check/{mediaType}/{mediaId}
// - Check if media is bookmarked
// - Path variables: mediaType, mediaId
// - Response: { "bookmarked": true/false }
// - Status: 200 OK
//
// GET /api/media/providers/{region}/{mediaId}
// - Get watch providers for media in specific region
// - Path variables: region (ISO code), mediaId
// - Response: WatchProviderDTO
// - Status: 200 OK
//
// GET /api/media/search
// - Search for media across types
// - Query params: q (query string), mediaType (optional), limit
// - Response: List<MediaMetadataDTO>
// - Status: 200 OK
//
// Dependencies:
// - FastAPIService (for media data)
// - WatchedService
// - RatingService
// - BookmarkService
//
// Response Enrichment:
// - When returning user's watched/rated/bookmarked items
// - Include media metadata from FastAPI/cache
// - Combine user state with media details
// - Example: WatchedEntry + MediaMetadata = EnrichedWatchedDTO
//
// Validation:
// - MediaType must be valid enum value
// - Rating must be 1-10
// - MediaId format validation
//
// Pagination:
// - Use Spring's Pageable
// - Default page size: 20
// - Max page size: 100
// - Include pagination metadata in response
//
// Error Handling:
// - InvalidMediaTypeException -> 400
// - MediaNotFoundException -> 404
// - ValidationException -> 400
// - Handled by global @ControllerAdvice