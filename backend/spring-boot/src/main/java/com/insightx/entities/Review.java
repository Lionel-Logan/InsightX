package com.insightx.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Review Entity - User written reviews for media
 */
@Entity
@Table(name = "reviews",
       uniqueConstraints = @UniqueConstraint(name = "uk_user_media_review",
                                            columnNames = {"user_id", "media_id", "media_type"}),
       indexes = {
           @Index(name = "idx_review_user", columnList = "user_id"),
           @Index(name = "idx_review_media", columnList = "media_id"),
           @Index(name = "idx_review_created", columnList = "created_at")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "Media ID is required")
    @Column(name = "media_id", nullable = false, length = 100)
    private String mediaId;

    @NotNull(message = "Media type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    private MediaType mediaType;

    @NotBlank(message = "Review text is required")
    @Size(min = 10, max = 5000, message = "Review must be between 10 and 5000 characters")
    @Column(name = "review_text", nullable = false, length = 5000)
    private String reviewText;

    @Column(name = "is_spoiler", nullable = false)
    @Builder.Default
    private Boolean isSpoiler = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", length = 20)
    @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;

    @Column(name = "upvote_count", nullable = false)
    @Builder.Default
    private Integer upvoteCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Visibility {
        PUBLIC, FOLLOWERS_ONLY, PRIVATE
    }
}