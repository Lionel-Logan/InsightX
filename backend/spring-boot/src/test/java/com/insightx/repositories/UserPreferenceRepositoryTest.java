package com.insightx.repositories;

import com.insightx.entities.UserPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserPreferenceRepository Tests")
class UserPreferenceRepositoryTest {

    @Autowired
    private UserPreferenceRepository repository;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        // Create test data
        UserPreference pref1 = UserPreference.builder()
            .userId(testUserId)
            .key("theme")
            .value("dark")
            .build();
        
        UserPreference pref2 = UserPreference.builder()
            .userId(testUserId)
            .key("language")
            .value("en")
            .build();
        
        repository.saveAll(List.of(pref1, pref2));
    }

    @Test
    @DisplayName("Should find all preferences by userId")
    void shouldFindAllPreferencesByUserId() {
        // When
        List<UserPreference> preferences = repository.findByUserId(testUserId);
        
        // Then
        assertThat(preferences).hasSize(2);
        assertThat(preferences).extracting(UserPreference::getKey)
            .containsExactlyInAnyOrder("theme", "language");
    }

    @Test
    @DisplayName("Should find preference by userId and key")
    void shouldFindPreferenceByUserIdAndKey() {
        // When
        Optional<UserPreference> preference = repository.findByUserIdAndKey(testUserId, "theme");
        
        // Then
        assertThat(preference).isPresent();
        assertThat(preference.get().getValue()).isEqualTo("dark");
    }

    @Test
    @DisplayName("Should check if preference exists")
    void shouldCheckIfPreferenceExists() {
        // When
        boolean exists = repository.existsByUserIdAndKey(testUserId, "theme");
        boolean notExists = repository.existsByUserIdAndKey(testUserId, "nonexistent");
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should delete preference by userId and key")
    void shouldDeletePreferenceByUserIdAndKey() {
        // When
        repository.deleteByUserIdAndKey(testUserId, "theme");
        
        // Then
        assertThat(repository.existsByUserIdAndKey(testUserId, "theme")).isFalse();
        assertThat(repository.countByUserId(testUserId)).isEqualTo(1);
    }

    @Test
    @DisplayName("Should delete all preferences for user")
    void shouldDeleteAllPreferencesForUser() {
        // When
        repository.deleteAllByUserId(testUserId);
        
        // Then
        assertThat(repository.countByUserId(testUserId)).isZero();
    }

    @Test
    @DisplayName("Should find multiple preferences by keys")
    void shouldFindMultiplePreferencesByKeys() {
        // When
        List<UserPreference> preferences = repository.findByUserIdAndKeyIn(
            testUserId, 
            List.of("theme", "language")
        );
        
        // Then
        assertThat(preferences).hasSize(2);
    }

    @Test
    @DisplayName("Should count preferences by userId")
    void shouldCountPreferencesByUserId() {
        // When
        long count = repository.countByUserId(testUserId);
        
        // Then
        assertThat(count).isEqualTo(2);
    }
}
