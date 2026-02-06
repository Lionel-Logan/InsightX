package com.insightx.repositories;

import com.insightx.entities.GenrePreference;
import com.insightx.entities.MediaType;
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
@DisplayName("GenrePreferenceRepository Tests")
class GenrePreferenceRepositoryTest {

    @Autowired
    private GenrePreferenceRepository repository;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        // Create test data
        GenrePreference pref1 = GenrePreference.builder()
            .userId(testUserId)
            .genre("Action")
            .mediaType(MediaType.MOVIE)
            .preferenceScore(9)
            .explicit(true)
            .build();
        
        GenrePreference pref2 = GenrePreference.builder()
            .userId(testUserId)
            .genre("Fantasy")
            .mediaType(MediaType.BOOK)
            .preferenceScore(8)
            .explicit(true)
            .build();
        
        GenrePreference pref3 = GenrePreference.builder()
            .userId(testUserId)
            .genre("RPG")
            .mediaType(MediaType.GAME)
            .preferenceScore(10)
            .explicit(false)  // Implicit
            .build();
        
        repository.saveAll(List.of(pref1, pref2, pref3));
    }

    @Test
    @DisplayName("Should find all genre preferences by userId")
    void shouldFindAllGenrePreferencesByUserId() {
        // When
        List<GenrePreference> preferences = repository.findByUserId(testUserId);
        
        // Then
        assertThat(preferences).hasSize(3);
    }

    @Test
    @DisplayName("Should find genre preferences by media type")
    void shouldFindGenrePreferencesByMediaType() {
        // When
        List<GenrePreference> moviePrefs = repository.findByUserIdAndMediaType(testUserId, MediaType.MOVIE);
        
        // Then
        assertThat(moviePrefs).hasSize(1);
        assertThat(moviePrefs.get(0).getGenre()).isEqualTo("Action");
    }

    @Test
    @DisplayName("Should find specific genre preference")
    void shouldFindSpecificGenrePreference() {
        // When
        Optional<GenrePreference> preference = repository.findByUserIdAndGenreAndMediaType(
            testUserId, "Action", MediaType.MOVIE
        );
        
        // Then
        assertThat(preference).isPresent();
        assertThat(preference.get().getPreferenceScore()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should find only explicit preferences")
    void shouldFindOnlyExplicitPreferences() {
        // When
        List<GenrePreference> explicitPrefs = repository.findByUserIdAndExplicitTrue(testUserId);
        
        // Then
        assertThat(explicitPrefs).hasSize(2);
        assertThat(explicitPrefs).extracting(GenrePreference::getExplicit)
            .containsOnly(true);
    }

    @Test
    @DisplayName("Should find only implicit preferences")
    void shouldFindOnlyImplicitPreferences() {
        // When
        List<GenrePreference> implicitPrefs = repository.findByUserIdAndExplicitFalse(testUserId);
        
        // Then
        assertThat(implicitPrefs).hasSize(1);
        assertThat(implicitPrefs.get(0).getGenre()).isEqualTo("RPG");
    }

    @Test
    @DisplayName("Should find high-scoring preferences")
    void shouldFindHighScoringPreferences() {
        // When
        List<GenrePreference> highScoring = repository.findByUserIdAndMinScore(testUserId, 9);
        
        // Then
        assertThat(highScoring).hasSize(2);  // Action (9) and RPG (10)
        assertThat(highScoring).extracting(GenrePreference::getPreferenceScore)
            .allMatch(score -> score >= 9);
    }

    @Test
    @DisplayName("Should get top N genres by media type")
    void shouldGetTopGenresByMediaType() {
        // Add more test data
        repository.save(GenrePreference.builder()
            .userId(testUserId)
            .genre("Comedy")
            .mediaType(MediaType.MOVIE)
            .preferenceScore(7)
            .explicit(true)
            .build());
        
        // When
        List<GenrePreference> topMovieGenres = repository.findTopGenresByMediaType(
            testUserId, MediaType.MOVIE, 2
        );
        
        // Then
        assertThat(topMovieGenres).hasSize(2);
        assertThat(topMovieGenres.get(0).getPreferenceScore())
            .isGreaterThanOrEqualTo(topMovieGenres.get(1).getPreferenceScore());
    }

    @Test
    @DisplayName("Should count explicit preferences by media type")
    void shouldCountExplicitPreferencesByMediaType() {
        // When
        long count = repository.countByUserIdAndMediaTypeAndExplicitTrue(testUserId, MediaType.MOVIE);
        
        // Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Should delete all explicit preferences")
    void shouldDeleteAllExplicitPreferences() {
        // When
        repository.deleteExplicitPreferences(testUserId);
        
        // Then
        List<GenrePreference> remaining = repository.findByUserId(testUserId);
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getExplicit()).isFalse();
    }

    @Test
    @DisplayName("Should delete all implicit preferences")
    void shouldDeleteAllImplicitPreferences() {
        // When
        repository.deleteImplicitPreferences(testUserId);
        
        // Then
        List<GenrePreference> remaining = repository.findByUserId(testUserId);
        assertThat(remaining).hasSize(2);
        assertThat(remaining).extracting(GenrePreference::getExplicit)
            .containsOnly(true);
    }

    @Test
    @DisplayName("Should get distinct genres by media type")
    void shouldGetDistinctGenresByMediaType() {
        // When
        List<String> movieGenres = repository.findDistinctGenresByMediaType(MediaType.MOVIE);
        
        // Then
        assertThat(movieGenres).contains("Action");
    }
}
