package com.insightx.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Bookmark Entity - User's saved/bookmarked media
 */
@Entity
@Table(name = "bookmarks",
       uniqueConstraints = @UniqueConstraint(name = "uk_user_media_bookmark",
                                            columnNames = {"user_id", "media_id", "media_type"}),
       indexes = {
           @Index(name = "idx_bookmark_user", columnList = "user_id"),
           @Index(name = "idx_bookmark_saved", columnList = "saved_at"),
           @Index(name = "idx_bookmark_user_saved", columnList = "user_id, saved_at")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark {

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

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(name = "notes", length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", length = 20)
    @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;

    @CreationTimestamp
    @Column(name = "saved_at", nullable = false, updatable = false)
    private LocalDateTime savedAt;

    public enum Visibility {
        PUBLIC, FOLLOWERS_ONLY, PRIVATE
    }
}