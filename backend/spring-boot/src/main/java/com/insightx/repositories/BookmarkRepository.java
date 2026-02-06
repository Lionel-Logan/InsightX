package com.insightx.repositories;

import com.insightx.entities.Bookmark;
import com.insightx.entities.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * BookmarkRepository - Data access for user bookmarks
 * 
 * Manages saved/bookmarked media with optional notes.
 * Used for watchlist, reading list, and wishlist features.
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    // ========================================
    // Basic Queries
    // ========================================

    /**
     * Get all bookmarks for user
     */
    List<Bookmark> findByUserId(UUID userId);

    /**
     * Get bookmarks in reverse chronological order (most recent first)
     */
    List<Bookmark> findByUserIdOrderBySavedAtDesc(UUID userId);

    /**
     * Get bookmarks filtered by media type
     */
    List<Bookmark> findByUserIdAndMediaType(UUID userId, MediaType mediaType);

    /**
     * Get specific bookmark
     */
    Optional<Bookmark> findByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);

    /**
     * Check if media is bookmarked by user
     */
    boolean existsByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);

    /**
     * Get recently bookmarked items (after specific date)
     */
    List<Bookmark> findByUserIdAndSavedAtAfter(UUID userId, LocalDateTime date);

    // ========================================
    // Pagination Support
    // ========================================

    /**
     * Paginated bookmarks, ordered by saved date
     */
    Page<Bookmark> findByUserIdOrderBySavedAtDesc(UUID userId, Pageable pageable);

    /**
     * Paginated bookmarks by media type
     */
    Page<Bookmark> findByUserIdAndMediaTypeOrderBySavedAtDesc(UUID userId, MediaType mediaType, Pageable pageable);

    // ========================================
    // Aggregation Queries
    // ========================================

    /**
     * Count total bookmarks for user
     */
    long countByUserId(UUID userId);

    /**
     * Count bookmarks by media type
     */
    long countByUserIdAndMediaType(UUID userId, MediaType mediaType);

    /**
     * Get bookmark count by media type (for statistics)
     */
    @Query("SELECT b.mediaType, COUNT(b) FROM Bookmark b WHERE b.userId = :userId GROUP BY b.mediaType")
    List<Object[]> getBookmarkCountByMediaType(@Param("userId") UUID userId);

    // ========================================
    // Delete Operations
    // ========================================

    /**
     * Remove bookmark
     */
    void deleteByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);

    /**
     * Clear all bookmarks for user
     */
    @Modifying
    @Query("DELETE FROM Bookmark b WHERE b.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}