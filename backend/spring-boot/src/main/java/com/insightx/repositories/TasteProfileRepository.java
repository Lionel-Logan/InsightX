package com.insightx.repositories;

import com.insightx.entities.TasteProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * TasteProfileRepository - Data access for taste profiles
 * 
 * Manages computed user preferences stored as JSONB.
 * Profile generation requires minimum 3 ratings.
 * Supports recency weighting and explicit/implicit preference merging.
 */
@Repository
public interface TasteProfileRepository extends JpaRepository<TasteProfile, UUID> {

    // ========================================
    // Basic Queries
    // ========================================

    /**
     * Get taste profile for user (one-to-one relationship)
     */
    Optional<TasteProfile> findByUserId(UUID userId);

    /**
     * Check if profile exists for user
     */
    boolean existsByUserId(UUID userId);

    // ========================================
    // Staleness Detection
    // ========================================

    /**
     * Find stale profiles needing recalculation (older than threshold)
     */
    List<TasteProfile> findByLastCalculatedBefore(LocalDateTime threshold);

    /**
     * Find profiles that need recalculation (either old or specific users)
     */
    @Query("SELECT tp FROM TasteProfile tp WHERE tp.lastCalculated < :threshold OR tp.userId IN :userIds")
    List<TasteProfile> findStaleOrSpecificProfiles(@Param("threshold") LocalDateTime threshold, 
                                                     @Param("userIds") List<UUID> userIds);

    /**
     * Count stale profiles
     */
    long countByLastCalculatedBefore(LocalDateTime threshold);

    // ========================================
    // Bulk Operations
    // ========================================

    /**
     * Update last calculated timestamp for multiple users
     */
    @Modifying
    @Query("UPDATE TasteProfile tp SET tp.lastCalculated = :date WHERE tp.userId IN :userIds")
    void updateLastCalculatedForUsers(@Param("userIds") List<UUID> userIds, @Param("date") LocalDateTime date);

    /**
     * Increment version for a profile (after recalculation)
     */
    @Modifying
    @Query("UPDATE TasteProfile tp SET tp.version = tp.version + 1 WHERE tp.userId = :userId")
    void incrementVersion(@Param("userId") UUID userId);

    // ========================================
    // Delete Operations
    // ========================================

    /**
     * Remove profile (will be regenerated on next request)
     */
    void deleteByUserId(UUID userId);

    /**
     * Delete old profiles (cleanup job)
     */
    @Modifying
    @Query("DELETE FROM TasteProfile tp WHERE tp.lastCalculated < :cutoffDate")
    void deleteOldProfiles(@Param("cutoffDate") LocalDateTime cutoffDate);
}