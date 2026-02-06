package com.insightx.repositories;

import com.insightx.entities.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserPreferenceRepository - Data access for user preferences
 * 
 * Handles key-value storage for user settings and privacy preferences.
 * Common keys: theme, language, explicit_content, notifications, etc.
 */
@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, UUID> {

    /**
     * Find all preferences for a user
     * Used during login to load all user settings
     */
    List<UserPreference> findByUserId(UUID userId);

    /**
     * Find specific preference by user and key
     */
    Optional<UserPreference> findByUserIdAndKey(UUID userId, String key);

    /**
     * Check if a preference exists
     */
    boolean existsByUserIdAndKey(UUID userId, String key);

    /**
     * Delete specific preference
     */
    void deleteByUserIdAndKey(UUID userId, String key);

    /**
     * Delete all preferences for a user (account deletion or reset)
     */
    @Modifying
    @Query("DELETE FROM UserPreference up WHERE up.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    /**
     * Batch retrieval of multiple preferences
     */
    @Query("SELECT up FROM UserPreference up WHERE up.userId = :userId AND up.key IN :keys")
    List<UserPreference> findByUserIdAndKeyIn(@Param("userId") UUID userId, @Param("keys") List<String> keys);

    /**
     * Count preferences for a user
     */
    long countByUserId(UUID userId);
}