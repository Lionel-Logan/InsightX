package com.insightx.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WatchedEntry Entity - Tracks user's watched/read/played media
 */
@Entity
@Table(name = "watched_entries",
       uniqueConstraints = @UniqueConstraint(name = "uk_user_media", 
                                            columnNames = {"user_id", "media_id", "media_type"}),
       indexes = {
           @Index(name = "idx_watched_user", columnList = "user_id"),
           @Index(name = "idx_watched_date", columnList = "watched_date"),
           @Index(name = "idx_watched_user_date", columnList = "user_id, watched_date")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchedEntry {

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

    @NotNull(message = "Watched date is required")
    @Column(name = "watched_date", nullable = false)
    @Builder.Default
    private LocalDate watchedDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", length = 20)
    @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Visibility {
        PUBLIC, FOLLOWERS_ONLY, PRIVATE
    }
}