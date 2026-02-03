package com.insightx.controllers;

// ReviewController - REST API endpoints for user reviews
// Base path: /api/reviews
// All endpoints require authentication (JWT)
//
// Endpoints:
//
// POST /api/reviews
// - Create a new review
// - Request body: CreateReviewRequest (mediaId, mediaType, reviewText, isSpoiler)
// - Response: ReviewDTO
// - Status: 201 Created
// - Errors: 400 if validation fails, 409 if review already exists
// - Note: Auto-marks media as watched
//
// GET /api/reviews/{reviewId}
// - Get a specific review by ID
// - Path variable: reviewId (UUID)
// - Response: ReviewDTO (includes author info)
// - Status: 200 OK
// - Errors: 404 if review not found
//
// PUT /api/reviews/{reviewId}
// - Update existing review
// - Path variable: reviewId
// - Request body: UpdateReviewRequest (reviewText, isSpoiler)
// - Response: ReviewDTO
// - Status: 200 OK
// - Errors: 404 if not found, 403 if not owner, 400 if validation fails
//
// DELETE /api/reviews/{reviewId}
// - Delete a review
// - Path variable: reviewId
// - Response: success message
// - Status: 200 OK
// - Errors: 404 if not found, 403 if not owner
//
// GET /api/reviews/user/{userId}
// - Get all reviews by a specific user
// - Path variable: userId
// - Query params: page, size, sort
// - Response: Page<ReviewDTO>
// - Status: 200 OK
// - Note: Public endpoint (anyone can see user's reviews - future feature)
//
// GET /api/reviews/my
// - Get current user's reviews
// - Query params: mediaType (optional), page, size, sort
// - Response: Page<ReviewDTO>
// - Status: 200 OK
//
// GET /api/reviews/media/{mediaType}/{mediaId}
// - Get all reviews for specific media
// - Path variables: mediaType, mediaId
// - Query params: excludeSpoilers (boolean), page, size, sort
// - Response: Page<ReviewDTO>
// - Status: 200 OK
// - Note: Future social feature - see what others think
//
// GET /api/reviews/check/{mediaType}/{mediaId}
// - Check if current user has reviewed this media
// - Path variables: mediaType, mediaId
// - Response: { "hasReview": true/false, "reviewId": UUID or null }
// - Status: 200 OK
//
// GET /api/reviews/recent
// - Get recent reviews from all users (future social feature)
// - Query params: mediaType (optional), limit
// - Response: List<ReviewDTO>
// - Status: 200 OK
//
// Dependencies:
// - ReviewService
// - UserService (for author information)
//
// Authorization:
// - Users can only edit/delete their own reviews
// - Extract userId from JWT token
// - Verify ownership before update/delete operations
//
// Validation:
// - reviewText: 10-5000 characters
// - isSpoiler: boolean (default false)
// - Use @Valid annotation with custom validators
// - Optional: Profanity filtering
// - Optional: Spam detection
//
// Response Format:
// - Include author information (username, avatar if available)
// - Include media information (title, poster)
// - Include timestamps (created, updated)
// - Include helpful count (future feature)
//
// Pagination:
// - Default page size: 20
// - Max page size: 50
// - Sort options: createdAt, updatedAt, helpfulCount (future)
//
// Error Handling:
// - ReviewNotFoundException -> 404
// - ReviewAlreadyExistsException -> 409
// - UnauthorizedAccessException -> 403
// - ValidationException -> 400
// - Handled by global @ControllerAdvice
//
// Future Enhancements:
// - POST /api/reviews/{reviewId}/helpful (mark review as helpful)
// - POST /api/reviews/{reviewId}/report (report inappropriate content)
// - GET /api/reviews/trending (most helpful recent reviews)
// - Rich text support
// - Image attachments