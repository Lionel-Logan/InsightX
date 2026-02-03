package com.insightx.services;

// UserService - Handles user profile and account management
//
// Responsibilities:
// - Get user profile
// - Update user profile
// - Change password
// - Update region
// - Delete account
// - Get user statistics
//
// Key Methods:
//
// getUserProfile(UUID userId): UserProfileDTO
// - Fetch user by ID
// - Map to DTO (exclude password)
// - Return user profile
//
// updateUserProfile(UUID userId, UpdateProfileRequest request): UserProfileDTO
// - Validate request
// - Update user fields
// - Save to database
// - Return updated profile
//
// changePassword(UUID userId, ChangePasswordRequest request): void
// - Verify current password
// - Validate new password strength
// - Hash new password
// - Update user
// - Invalidate existing tokens (optional)
//
// updateRegion(UUID userId, String region): void
// - Update user's region
// - Invalidate cached recommendations
// - Trigger taste profile recalculation
//
// deleteAccount(UUID userId): void
// - Soft delete or hard delete (decide based on requirements)
// - Delete related data (cascade should handle this)
// - Invalidate tokens
//
// getUserStatistics(UUID userId): UserStatsDTO
// - Get total ratings count
// - Get total reviews count
// - Get total watched items
// - Get total bookmarks
// - Get account age
// - Return aggregated stats
//
// Dependencies:
// - UserRepository
// - PasswordEncoder
// - RatingRepository (for stats)
// - ReviewRepository (for stats)
// - WatchedEntryRepository (for stats)
// - BookmarkRepository (for stats)
//
// Validation:
// - Email format validation
// - Username format (3-50 chars, alphanumeric + underscore)
// - Region code validation (ISO country codes)
// - Password strength requirements
//
// Transaction Management:
// - Use @Transactional for update operations
// - Read-only for retrieval operations
//
// Error Handling:
// - UserNotFoundException
// - InvalidPasswordException
// - ValidationException