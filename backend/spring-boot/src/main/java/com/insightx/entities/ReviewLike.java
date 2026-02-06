package com.insightx.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ReviewLike Entity - Tracks user likes/upvotes on reviews
 * 
 * Features:
 * - Instagram-style upvote system (like/unlike)
 * - One vote per user per review
 * - Cascade delete when review or user is deleted
 * - Denormalized count stored in Review.upvoteCount
 * 
 * Usage:
 * - User can like a review to show appreciation
 * - User can undo their like
 * - Reviews sorted by upvote count for best content first
 */
@Entity
@Table(name = "review_likes",
       uniqueConstraints = @UniqueConstraint(name = "uk_review_user_like",
                                            columnNames = {"review_id", "user_id"}),
       indexes = {
           @Index(name = "idx_review_likes_review", columnList = "review_id"),
           @Index(name = "idx_review_likes_user", columnList = "user_id"),
           @Index(name = "idx_review_likes_created", columnList = "created_at")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Review ID is required")
    @Column(name = "review_id", nullable = false)
    private UUID reviewId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewLike)) return false;
        ReviewLike that = (ReviewLike) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ReviewLike{" +
                "id=" + id +
                ", reviewId=" + reviewId +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}
