package com.insightx.services;

// FastAPIService - Integration service for FastAPI Intelligence Layer
// Handles all communication with the Python FastAPI backend
//
// Responsibilities:
// - Fetch media metadata from FastAPI
// - Get personalized recommendations
// - Get similar media suggestions
// - Fetch watch provider information
// - Request AI explanations
// - Extract themes from user profile
// - Handle retries and circuit breaking
//
// Key Methods:
//
// getMediaMetadata(String mediaId, MediaType mediaType): Optional<MediaMetadataDTO>
// - GET /api/media/{type}/{id}
// - Fetch complete media information
// - Check Redis cache first
// - Cache response for 2 hours
// - Return Optional.empty() on error
//
// getRecommendations(UUID userId, MediaType mediaType, int limit): List<RecommendationDTO>
// - POST /api/recommendations/user/{userId}
// - Send user's taste profile
// - Receive ranked recommendations with explanation signals
// - Check cache first (30 min TTL)
// - Return empty list on error
//
// getSimilarMedia(String mediaId, MediaType mediaType, int limit): List<MediaMetadataDTO>
// - POST /api/recommendations/similar
// - Find media similar to given item
// - Based on genre, themes, creators
// - Cache results (1 hour)
//
// getWatchProviders(String mediaId, String region): WatchProviderDTO
// - GET /api/providers/{region}/{mediaId}
// - Get streaming/purchase availability
// - Region-specific results
// - Cache heavily (1 hour) as this changes slowly
//
// getAIExplanation(String mediaId, UUID userId): Optional<String>
// - POST /api/ai/explain
// - Get AI-generated explanation for recommendation
// - Send user profile context
// - Cache explanation (1 hour)
// - Return Optional.empty() on error
//
// extractThemes(List<String> mediaIds): Map<String, List<String>>
// - POST /api/themes/extract
// - Batch extract themes from multiple media items
// - Used during taste profile generation
// - Return map of mediaId -> themes
//
// searchMedia(String query, MediaType mediaType, int limit): List<MediaMetadataDTO>
// - GET /api/search
// - Search across media types
// - Return top results
// - Cache common searches
//
// Dependencies:
// - WebClient (configured in WebClientConfig)
// - RedisTemplate (for caching)
// - ObjectMapper (for JSON serialization)
//
// Request Configuration:
// - Base URL: http://localhost:8000 (configurable)
// - Timeout: 30 seconds
// - Retry: 3 attempts with exponential backoff
// - Service token: X-Service-Token header
//
// Cache Strategy:
// - Cache all responses in Redis
// - Different TTLs per endpoint type:
//   * Media metadata: 2 hours
//   * Recommendations: 30 minutes
//   * Watch providers: 1 hour
//   * AI explanations: 1 hour
//   * Search results: 15 minutes
// - Cache keys format: "fastapi:{endpoint}:{params}"
//
// Error Handling:
// - Catch WebClientException
// - Log error with full request details
// - Return empty Optional or empty list
// - Don't propagate exceptions to controllers
// - Implement circuit breaker for repeated failures
//
// Circuit Breaker Pattern:
// - Track failure rate per endpoint
// - If failures > threshold, open circuit
// - Return cached/fallback data when circuit open
// - Automatically retry after cooldown period
//
// Response Validation:
// - Validate response structure
// - Handle malformed JSON gracefully
// - Log validation errors
//
// Monitoring & Logging:
// - Log all requests (URL, params, duration)
// - Log cache hits/misses
// - Track response times
// - Alert on high error rates
//
// Async Processing:
// - Consider using @Async for non-critical calls
// - Don't block main request thread
// - Use CompletableFuture for parallel requests
//
// Testing:
// - Mock WebClient for unit tests
// - Use WireMock for integration tests
// - Test retry and timeout scenarios
//
// Future Enhancements:
// - GraphQL support
// - WebSocket for real-time updates
// - Batch request optimization
// - Request deduplication
// - Adaptive timeouts based on endpoint