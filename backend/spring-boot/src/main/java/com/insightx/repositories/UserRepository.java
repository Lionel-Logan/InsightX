package com.insightx.repositories;

// UserRepository - Data access layer for User entity
// Extends JpaRepository for CRUD operations
//
// Custom Query Methods:
// - findByUsername(String username): Optional<User>
//   Find user by username (for login)
//
// - findByEmail(String email): Optional<User>
//   Find user by email (for login, duplicate check)
//
// - existsByUsername(String username): boolean
//   Check if username exists (for registration validation)
//
// - existsByEmail(String email): boolean
//   Check if email exists (for registration validation)
//
// - findByRegion(String region): List<User>
//   Find all users in a region (for analytics)
//
// - findByActiveTrue(): List<User>
//   Find all active users (exclude soft-deleted)
//
// - findByCreatedAtBetween(LocalDateTime start, LocalDateTime end): List<User>
//   Find users created in date range (for analytics)
//
// Query Method Features:
// - Use Spring Data JPA method name conventions
// - No need for @Query annotations for simple queries
// - Return Optional<User> for single results to handle null safely
//
// Pagination Support:
// - Use Pageable parameter for methods that return lists
// - Example: Page<User> findByRegion(String region, Pageable pageable)
//
// Transaction Management:
// - @Transactional at service layer, not repository
// - Read-only transactions for query methods
//
// Performance Considerations:
// - Add @EntityGraph for fetching relationships
// - Use projections for specific field retrieval
// - Consider query caching for frequently accessed data