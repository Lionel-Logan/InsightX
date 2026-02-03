package com.insightx.services;

// ReviewService - Manages user reviews for media content
//
// Responsibilities:
// - Create new review
// - Update existing review
// - Delete review
// - Get user's reviews
// - Get reviews for media (future social feature)
// - Moderate reviews (future)
//
// Key Methods:
//
// createReview(UUID userId, ReviewRequest request): ReviewDTO
// - Validate review text (10-5000 chars)
// - Check if review already exists
// - Create new Review entity
// - Save to database
// - Optional: Auto-mark media as watched
// - Return review DTO
//
// updateReview(UUID userId, UUID reviewId, UpdateReviewRequest request): ReviewDTO
// - Find existing review
// - Verify ownership (userId must match)
// - Update review text and spoiler flag
// - Update timestamp
// - Save changes
// - Return updated review
//
// deleteReview(UUID userId, UUID reviewId): void
// - Find review
// - Verify ownership
// - Delete from database (hard delete or soft delete)
//
// getUserReviews(UUID userId, Pageable pageable): Page<ReviewDTO>
// - Get all reviews by user
// - Order by date (newest first)
// - Return paginated results
//
// getReviewsForMedia(String mediaId, MediaType mediaType, boolean excludeSpoilers, Pageable pageable): Page<ReviewDTO>
// - Get all reviews for specific media
// - Optional: Filter out spoilers
// - Order by helpful count or date (future enhancement)
// - Return paginated results
//
// getReviewById(UUID reviewId): ReviewDTO
// - Fetch single review by ID
// - Include user information
//
// getUserReviewForMedia(UUID userId, String mediaId, MediaType mediaType): Optional<ReviewDTO>
// - Check if user has reviewed specific media
// - Return Optional.empty() if not
//
// Dependencies:
// - ReviewRepository
// - UserRepository (for author information)
// - WatchedService (optional: auto-mark as watched)
//
// Validation:
// - Review text: 10-5000 characters
// - Profanity filtering (optional, use external library)
// - Spam detection (future)
//
// Business Rules:
// - User can only review media once (update if exists)
// - Review implies consumption
// - User can edit their own reviews
// - User can delete their own reviews
// - Spoiler flag should be prominently displayed in UI
//
// Future Enhancements:
// - Helpful/unhelpful voting
// - Report inappropriate reviews
// - Moderation workflow
// - Rich text formatting
// - Media attachments (images)
//
// Transaction Management:
// - @Transactional for write operations
//
// Error Handling:
// - ReviewAlreadyExistsException
// - ReviewNotFoundException
// - UnauthorizedAccessException (editing other's review)
// - ValidationException
//
// Search & Discovery (Future):
// - Full-text search on review content
// - Filter by rating score
// - Sort by helpfulness