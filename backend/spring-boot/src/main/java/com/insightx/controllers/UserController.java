package com.insightx.controllers;

// UserController - REST API endpoints for user management
// Base path: /api/users
// All endpoints require authentication (JWT)
//
// Endpoints:
//
// GET /api/users/profile
// - Get current user's profile
// - Response: UserProfileDTO
// - Status: 200 OK
//
// PUT /api/users/profile
// - Update current user's profile
// - Request body: UpdateProfileRequest (username, email)
// - Response: UserProfileDTO
// - Status: 200 OK
// - Errors: 400 if username/email taken, 400 if validation fails
//
// PUT /api/users/region
// - Update user's region
// - Request body: UpdateRegionRequest (region: ISO country code)
// - Response: success message
// - Status: 200 OK
// - Note: Invalidates cached recommendations
//
// PUT /api/users/password
// - Change password
// - Request body: ChangePasswordRequest (currentPassword, newPassword)
// - Response: success message
// - Status: 200 OK
// - Errors: 401 if current password wrong, 400 if validation fails
//
// GET /api/users/statistics
// - Get user's statistics (ratings count, reviews, watched items, etc.)
// - Response: UserStatsDTO
// - Status: 200 OK
//
// DELETE /api/users/account
// - Delete user account
// - Request body: DeleteAccountRequest (password for confirmation)
// - Response: success message
// - Status: 200 OK
// - Note: Soft delete or hard delete based on business requirements
//
// GET /api/users/{userId}/public-profile
// - Get public profile of any user (future feature)
// - Response: PublicUserProfileDTO (limited info)
// - Status: 200 OK
// - Errors: 404 if user not found
//
// Dependencies:
// - UserService
// - AuthService (for extracting current user from JWT)
//
// Security:
// - All endpoints require valid JWT token
// - User can only modify their own profile
// - Extract userId from JWT token (SecurityContext)
// - Don't trust userId from request parameters for sensitive operations
//
// Validation:
// - Email format validation
// - Username: 3-50 chars, alphanumeric + underscore
// - Password: min 8 chars, must contain uppercase, lowercase, digit
// - Region: valid ISO country code
//
// Error Handling:
// - UserNotFoundException -> 404
// - UnauthorizedException -> 401
// - ValidationException -> 400
// - Handled by global @ControllerAdvice
//
// Response Format:
// - Consistent with AuthController
// - Include timestamp and request ID