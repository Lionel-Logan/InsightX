package com.insightx.services;

// TasteProfileService - Generates and manages user taste profiles
// This is a core service that powers personalized recommendations
//
// Responsibilities:
// - Generate taste profile from user ratings
// - Update profile incrementally
// - Cache profile for performance
// - Provide profile data to recommendation engine
// - Detect profile staleness
//
// Key Methods:
//
// getTasteProfile(UUID userId): TasteProfileDTO
// - Check Redis cache first
// - If not in cache, load from database
// - If doesn't exist or stale, generate new profile
// - Cache and return
//
// generateTasteProfile(UUID userId): TasteProfileDTO
// - Fetch all user ratings
// - Require minimum 5 ratings to generate meaningful profile
// - Calculate genre preferences
// - Calculate theme affinities
// - Identify favorite creators
// - Analyze rating behavior
// - Calculate media type distribution
// - Store as JSON in database
// - Cache in Redis
// - Return profile
//
// updateTasteProfile(UUID userId): TasteProfileDTO
// - Similar to generate but faster (incremental if possible)
// - Triggered after new ratings
// - Invalidate cache
// - Return updated profile
//
// isProfileStale(UUID userId): boolean
// - Check if profile is older than threshold (7 days)
// - Check if user has new ratings since last calculation
// - Return true if needs recalculation
//
// invalidateProfile(UUID userId): void
// - Delete from Redis cache
// - Mark as needing recalculation
// - Useful when user changes region or preferences
//
// getGenrePreferences(UUID userId): Map<String, Double>
// - Extract genre preferences from profile
// - Scores from 0.0 to 1.0
//
// Profile Generation Algorithm:
//
// 1. Genre Preferences:
//    - Count ratings per genre
//    - Calculate average rating per genre
//    - Weight by recency (recent ratings weighted higher)
//    - Normalize to 0-1 scale
//    - Example: {"Action": 0.85, "Drama": 0.65, "Sci-Fi": 0.90}
//
// 2. Theme Affinities:
//    - Extract themes from highly rated content (rating >= 8)
//    - Use FastAPI theme extraction service
//    - Calculate frequency and average rating per theme
//    - Example: {"time-travel": 0.75, "redemption": 0.60}
//
// 3. Favorite Creators:
//    - Identify directors, authors, developers from highly rated content
//    - Rank by average rating and frequency
//    - Store top 10
//
// 4. Rating Behavior:
//    - Calculate average rating
//    - Detect harsh critic (avg < 6) or generous rater (avg > 8)
//    - Calculate rating variance (diverse interests vs consistent)
//    - Rating velocity (ratings per month)
//
// 5. Media Type Distribution:
//    - Percentage of ratings per media type
//    - Helps balance cross-media recommendations
//
// Dependencies:
// - RatingRepository (to fetch user ratings)
// - TasteProfileRepository (to persist profiles)
// - FastAPIService (to fetch media metadata for theme extraction)
// - RedisTemplate (for caching)
//
// Cache Strategy:
// - Redis key: "taste_profile:{userId}"
// - TTL: 1 hour
// - Invalidate on profile updates
// - Store as JSON string
//
// Recalculation Triggers:
// - On-demand (user requests or API call)
// - Scheduled job for stale profiles (daily)
// - After every 5 new ratings
// - When user changes region
//
// Cold Start Problem (< 5 ratings):
// - Return generic/popular recommendations
// - Show onboarding flow to collect initial ratings
// - Gradually personalize as more ratings collected
//
// Transaction Management:
// - @Transactional for profile save operations
// - Read-only for profile retrieval
//
// Error Handling:
// - InsufficientDataException (< 5 ratings)
// - ProfileGenerationException
// - UserNotFoundException
//
// Performance Considerations:
// - Profile generation can be expensive (async processing)
// - Use CompletableFuture for async updates
// - Don't block API responses waiting for profile
// - Return cached profile, update in background
//
// Future Enhancements:
// - Collaborative filtering (similar users)
// - Temporal evolution tracking
// - Profile explainability (why these preferences?)
// - A/B testing different profile algorithms