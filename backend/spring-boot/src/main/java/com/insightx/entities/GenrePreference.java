package com.insightx.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * GenrePreference Entity - User genre preferences for personalized recommendations
 * 
 * Features:
 * - 1-10 scoring system (1 = dislike, 10 = love)
 * - Supports explicit (user-selected) and implicit (calculated) preferences
 * - Media-type specific (Movies, Books, Games can have different genre preferences)
 * - Decay strategy: Explicit preferences fade as user rates more content
 * 
 * Usage:
 * - Onboarding: User selects favorite genres (score = 10)
 * - Skip onboarding: Top 3 worldwide genres assigned (score = 7)
 * - Implicit calculation: Updated based on rating patterns with recency weighting
 * - Merged scoring: 70% implicit + 30% explicit (with decay)
 */
@Entity
@Table(name = "genre_preferences",
       uniqueConstraints = @UniqueConstraint(name = "uk_user_genre_media",
                                            columnNames = {"user_id", "genre", "media_type"}),
       indexes = {
           @Index(name = "idx_genre_pref_user", columnList = "user_id"),
           @Index(name = "idx_genre_pref_media_type", columnList = "media_type"),
           @Index(name = "idx_genre_pref_score", columnList = "preference_score"),
           @Index(name = "idx_genre_pref_explicit", columnList = "explicit"),
           @Index(name = "idx_genre_pref_genre", columnList = "genre")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenrePreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "Genre is required")
    @Column(name = "genre", nullable = false, length = 100)
    private String genre;

    @NotNull(message = "Media type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    private MediaType mediaType;

    @NotNull(message = "Preference score is required")
    @Min(value = 1, message = "Preference score must be at least 1")
    @Max(value = 10, message = "Preference score must not exceed 10")
    @Column(name = "preference_score", nullable = false)
    @Builder.Default
    private Integer preferenceScore = 5;

    @NotNull(message = "Explicit flag is required")
    @Column(name = "explicit", nullable = false)
    @Builder.Default
    private Boolean explicit = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenrePreference)) return false;
        GenrePreference that = (GenrePreference) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "GenrePreference{" +
                "id=" + id +
                ", userId=" + userId +
                ", genre='" + genre + '\'' +
                ", mediaType=" + mediaType +
                ", preferenceScore=" + preferenceScore +
                ", explicit=" + explicit +
                '}';
    }
}
