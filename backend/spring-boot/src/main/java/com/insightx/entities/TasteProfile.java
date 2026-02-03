package com.insightx.entities;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * TasteProfile Entity - Derived user preferences and taste fingerprint
 */
@Entity
@Table(name = "taste_profiles",
       indexes = {
           @Index(name = "idx_taste_profile_user", columnList = "user_id", unique = true),
           @Index(name = "idx_taste_profile_calculated", columnList = "last_calculated")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TasteProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", unique = true, nullable = false)
    private UUID userId;

    @Type(JsonBinaryType.class)
    @Column(name = "profile_data", columnDefinition = "jsonb")
    private Map<String, Object> profileData;

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    @CreationTimestamp
    @Column(name = "last_calculated", nullable = false)
    private LocalDateTime lastCalculated;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}