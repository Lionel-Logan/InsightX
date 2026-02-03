package com.insightx.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserPreference Entity - Key-Value storage for user preferences and privacy settings
 */
@Entity
@Table(name = "user_preferences", 
       uniqueConstraints = @UniqueConstraint(name = "uk_user_key", columnNames = {"user_id", "preference_key"}),
       indexes = @Index(name = "idx_user_preference_user", columnList = "user_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "Preference key is required")
    @Size(max = 100, message = "Key must not exceed 100 characters")
    @Column(name = "preference_key", nullable = false, length = 100)
    private String key;

    @Size(max = 1000, message = "Value must not exceed 1000 characters")
    @Column(name = "preference_value", length = 1000)
    private String value;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}