package com.insightx.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserFollow Entity - Manages follow relationships between users
 * Enables social networking features
 */
@Entity
@Table(name = "user_follows",
       uniqueConstraints = @UniqueConstraint(name = "uk_follower_following",
                                            columnNames = {"follower_id", "following_id"}),
       indexes = {
           @Index(name = "idx_follow_follower", columnList = "follower_id"),
           @Index(name = "idx_follow_following", columnList = "following_id"),
           @Index(name = "idx_follow_created", columnList = "created_at")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Follower ID is required")
    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @NotNull(message = "Following ID is required")
    @Column(name = "following_id", nullable = false)
    private UUID followingId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserFollow)) return false;
        UserFollow that = (UserFollow) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
