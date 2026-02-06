package com.insightx.repositories;

import com.insightx.entities.MediaType;
import com.insightx.entities.WatchedEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * WatchedEntryRepository - Data access for watched media tracking
 * 
 * Tracks when users have watched/read/played media content.
 * Used for watch history, activity feeds, and recommendation exclusion.
 */
@Repository
public interface WatchedEntryRepository extends JpaRepository<WatchedEntry, UUID> {

    // ========================================
    // Basic Queries
    // ========================================

    /**
     * Get all watched entries for a user
     */
    List<WatchedEntry> findByUserId(UUID userId);

    /**
     * Get watched entries filtered by media type
     */
    List<WatchedEntry> findByUserIdAndMediaType(UUID userId, MediaType mediaType);

    /**
     * Check if user has watched specific media
     */
    Optional<WatchedEntry> findByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);

    /**
     * Quick existence check (more efficient than findBy)
     */
    boolean existsByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);

    // ========================================
    // Chronological Queries
    // ========================================

    /**
     * Get watch history in reverse chronological order
     */
    List<WatchedEntry> findByUserIdOrderByWatchedDateDesc(UUID userId);

    /**
     * Get recently watched items (after specific date)
     */
    List<WatchedEntry> findByUserIdAndWatchedDateAfter(UUID userId, LocalDate date);

    /**
     * Get watched entries within date range
     */
    List<WatchedEntry> findByUserIdAndWatchedDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);

    // ========================================
    // Pagination Support
    // ========================================

    /**
     * Paginated watch history
     */
    Page<WatchedEntry> findByUserId(UUID userId, Pageable pageable);

    /**
     * Paginated watch history by media type
     */
    Page<WatchedEntry> findByUserIdAndMediaType(UUID userId, MediaType mediaType, Pageable pageable);

    /**
     * Paginated watch history ordered by date
     */
    Page<WatchedEntry> findByUserIdOrderByWatchedDateDesc(UUID userId, Pageable pageable);

    // ========================================
    // Aggregation Queries
    // ========================================

    /**
     * Count total watched items for user
     */
    long countByUserId(UUID userId);

    /**
     * Count watched items by media type
     */
    long countByUserIdAndMediaType(UUID userId, MediaType mediaType);

    /**
     * Count watched items in date range
     */
    long countByUserIdAndWatchedDateAfter(UUID userId, LocalDate date);

    /**
     * Get watched count by media type (for statistics)
     */
    @Query("SELECT we.mediaType, COUNT(we) FROM WatchedEntry we WHERE we.userId = :userId GROUP BY we.mediaType")
    List<Object[]> getWatchedCountByMediaType(@Param("userId") UUID userId);

    // ========================================
    // Activity Feed Support
    // ========================================

    /**
     * Get recent watched entries by multiple users (for activity feed)
     * Only fetches PUBLIC visibility entries
     */
    @Query("SELECT we FROM WatchedEntry we WHERE we.userId IN :userIds " +
           "AND we.visibility = 'PUBLIC' " +
           "AND we.createdAt > :since " +
           "ORDER BY we.createdAt DESC")
    List<WatchedEntry> findRecentByUserIdsPublic(@Param("userIds") List<UUID> userIds, 
                                                  @Param("since") LocalDateTime since);

    // ========================================
    // Delete Operations
    // ========================================

    /**
     * Remove watched entry (un-watch feature)
     */
    void deleteByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);
}