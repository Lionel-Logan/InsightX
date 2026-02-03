package com.insightx.controllers;

// RecommendationController - REST API endpoints for recommendations
// Base path: /api/recommendations
// All endpoints require authentication (JWT)
//
// Endpoints:
//
// GET /api/recommendations
// - Get personalized recommendations for current user
// - Query params: 
//   * mediaType (optional: movie|book|game|all)
//   * limit (default: 20, max: 100)
//   * excludeWatched (boolean, default: true)
// - Response: List<RecommendationDTO> with explanation signals
// - Status: 200 OK
// - Note: Uses user's taste profile
//
// GET /api/recommendations/similar/{mediaType}/{mediaId}
// - Get media similar to given item
// - Path variables: mediaType, mediaId
// - Query params: limit (default: 10)
// - Response: List<MediaMetadataDTO>
// - Status: 200 OK
// - Errors: 404 if media not found
//
// GET /api/recommendations/trending/{mediaType}
// - Get trending/popular media (cold start recommendations)
// - Path variable: mediaType (movie|book|game)
// - Query params: region (optional, defaults to user's region), limit
// - Response: List<MediaMetadataDTO>
// - Status: 200 OK
//
// GET /api/recommendations/new-releases/{mediaType}
// - Get newly released media
// - Path variable: mediaType
// - Query params: region (optional), limit
// - Response: List<MediaMetadataDTO>
// - Status: 200 OK
//
// POST /api/recommendations/explain
// - Get AI explanation for why media is recommended
// - Request body: ExplainRequest (mediaId, mediaType)
// - Response: { "explanation": "AI-generated text" }
// - Status: 200 OK
// - Note: Uses user's taste profile as context
//
// GET /api/recommendations/for-you
// - Get "For You" section recommendations
// - Mix of personalized, trending, and new releases
// - Response: ForYouDTO (multiple sections)
// - Status: 200 OK
//
// GET /api/recommendations/genres/{genre}
// - Get top-rated media in specific genre
// - Path variable: genre (e.g., "action", "sci-fi")
// - Query params: mediaType (optional), limit
// - Response: List<MediaMetadataDTO>
// - Status: 200 OK
//
// POST /api/recommendations/refresh
// - Force refresh user's recommendations
// - Invalidates cache and regenerates
// - Response: success message
// - Status: 200 OK
//
// GET /api/recommendations/taste-profile
// - Get user's taste profile (for transparency)
// - Response: TasteProfileDTO (genre preferences, themes, favorites)
// - Status: 200 OK
// - Note: Shows user why they get certain recommendations
//
// Dependencies:
// - FastAPIService (for generating recommendations)
// - TasteProfileService (for user taste profile)
// - WatchedService (for filtering watched items)
// - RatingService (for preference data)
//
// Recommendation Logic Flow:
// 1. Controller receives request
// 2. Get/generate user's taste profile
// 3. Call FastAPI with taste profile
// 4. FastAPI returns ranked recommendations with signals
// 5. Filter out watched items if requested
// 6. Enrich with user state (bookmarked, rated)
// 7. Return to client
//
// Cache Strategy:
// - Cache recommendations in Redis (TTL: 30 minutes)
// - Cache key includes: userId, mediaType, excludeWatched flag
// - Invalidate on: new rating, new watched item, profile refresh
// - For trending/new releases: cache longer (2 hours)
//
// Cold Start Handling:
// - If user has < 5 ratings, show trending/popular content
// - Gradually personalize as more ratings collected
// - Encourage user to rate more items
//
// Explanation Signals (from FastAPI):
// - Genre match score
// - Theme similarity
// - Creator overlap (director, author, developer)
// - Similar user preferences
// - User's rating prediction
//
// Response Format:
// - Include media metadata
// - Include explanation snippet
// - Include confidence score
// - Include why recommended (genre match, similar to X, etc.)
//
// Pagination:
// - Most endpoints return lists, not pages
// - Use limit parameter for control
// - Consider pagination for large result sets
//
// Error Handling:
// - InsufficientDataException -> Return trending instead
// - FastAPIServiceException -> Return cached or fallback recommendations
// - Handled by global @ControllerAdvice
//
// Performance:
// - Async recommendation generation
// - Don't block on taste profile recalculation
// - Return cached recommendations quickly
// - Update in background
//
// Future Enhancements:
// - Personalized genres/themes discovery
// - "Because you watched X" recommendations
// - Collaborative filtering
// - Social recommendations (friends' favorites)
// - Real-time recommendations based on current mood