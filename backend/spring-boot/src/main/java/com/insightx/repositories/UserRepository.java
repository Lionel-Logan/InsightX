package com.insightx.repositories;

import com.insightx.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Repository - Data access layer for User entity
 * 
 * Provides CRUD operations and custom query methods for User management
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username (case-insensitive)
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all users in a specific region
     */
    List<User> findByRegion(String region);

    /**
     * Find all active users
     */
    List<User> findByActiveTrue();

    /**
     * Find all verified users
     */
    List<User> findByEmailVerifiedTrue();

    /**
     * Find users created within a date range
     */
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find users by role
     */
    List<User> findByRole(String role);
}