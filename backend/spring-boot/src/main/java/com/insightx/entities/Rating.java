package com.insightx.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Rating Entity - User ratings for media content
 */
@Entity
@Table(name = "ratings",
       uniqueConstraints = @UniqueConstraint(name = "uk_user_media_rating",
                                            columnNames = {"user_id", "media_id", "media_type"}),
       indexes = {
           @Index(name = "idx_rating_user", columnList = "user_id"),
           @Index(name = "idx_rating_created", columnList = "created_at"),
           @Index(name = "idx_rating_value", columnList = "rating")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

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

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must not exceed 10")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", length = 20)
    @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;

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