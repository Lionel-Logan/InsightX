package com.insightx.repositories;

import com.insightx.entities.MediaType;
import com.insightx.entities.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("RatingRepository Tests")
class RatingRepositoryTest {

    @Autowired
    private RatingRepository repository;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        // Create test data
        Rating rating1 = Rating.builder()
            .userId(testUserId)
            .mediaId("movie1")
            .mediaType(MediaType.MOVIE)
            .rating(9)
            .visibility(Rating.Visibility.PUBLIC)
            .build();
        
        Rating rating2 = Rating.builder()
            .userId(testUserId)
            .mediaId("book1")
            .mediaType(MediaType.BOOK)
            .rating(7)
            .visibility(Rating.Visibility.PUBLIC)
            .build();
        
        Rating rating3 = Rating.builder()
            .userId(testUserId)
            .mediaId("game1")
            .mediaType(MediaType.GAME)
            .rating(10)
            .visibility(Rating.Visibility.PRIVATE)
            .build();
        
        repository.saveAll(List.of(rating1, rating2, rating3));
    }

    @Test
    @DisplayName("Should find all ratings by userId")
    void shouldFindAllRatingsByUserId() {
        // When
        List<Rating> ratings = repository.findByUserId(testUserId);
        
        // Then
        assertThat(ratings).hasSize(3);
    }

    @Test
    @DisplayName("Should find ratings by media type")
    void shouldFindRatingsByMediaType() {
        // When
        List<Rating> movieRatings = repository.findByUserIdAndMediaType(testUserId, MediaType.MOVIE);
        
        // Then
        assertThat(movieRatings).hasSize(1);
        assertThat(movieRatings.get(0).getMediaId()).isEqualTo("movie1");
    }

    @Test
    @DisplayName("Should find specific rating")
    void shouldFindSpecificRating() {
        // When
        Optional<Rating> rating = repository.findByUserIdAndMediaIdAndMediaType(
            testUserId, "movie1", MediaType.MOVIE
        );
        
        // Then
        assertThat(rating).isPresent();
        assertThat(rating.get().getRating()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should check if user has rated media")
    void shouldCheckIfUserHasRatedMedia() {
        // When
        boolean hasRated = repository.existsByUserIdAndMediaIdAndMediaType(
            testUserId, "movie1", MediaType.MOVIE
        );
        boolean hasNotRated = repository.existsByUserIdAndMediaIdAndMediaType(
            testUserId, "movie999", MediaType.MOVIE
        );
        
        // Then
        assertThat(hasRated).isTrue();
        assertThat(hasNotRated).isFalse();
    }

    @Test
    @DisplayName("Should find ratings ordered by date")
    void shouldFindRatingsOrderedByDate() {
        // When
        List<Rating> ratings = repository.findByUserIdOrderByCreatedAtDesc(testUserId);
        
        // Then
        assertThat(ratings).hasSize(3);
        // Verify descending order
        for (int i = 0; i < ratings.size() - 1; i++) {
            assertThat(ratings.get(i).getCreatedAt())
                .isAfterOrEqualTo(ratings.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("Should find highly rated items")
    void shouldFindHighlyRatedItems() {
        // When
        List<Rating> highlyRated = repository.findByUserIdAndRatingGreaterThanEqual(testUserId, 8);
        
        // Then
        assertThat(highlyRated).hasSize(2);  // ratings 9 and 10
        assertThat(highlyRated).extracting(Rating::getRating)
            .allMatch(rating -> rating >= 8);
    }

    @Test
    @DisplayName("Should get top rated items")
    void shouldGetTopRatedItems() {
        // When
        List<Rating> topRated = repository.findTopRatedByUser(testUserId, 2);
        
        // Then
        assertThat(topRated).hasSize(2);
        assertThat(topRated.get(0).getRating()).isEqualTo(10);
        assertThat(topRated.get(1).getRating()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should support pagination")
    void shouldSupportPagination() {
        // When
        Page<Rating> page = repository.findByUserId(testUserId, PageRequest.of(0, 2));
        
        // Then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should count ratings by userId")
    void shouldCountRatingsByUserId() {
        // When
        long count = repository.countByUserId(testUserId);
        
        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should calculate average rating")
    void shouldCalculateAverageRating() {
        // When
        Double avgRating = repository.getAverageRatingByUserId(testUserId);
        
        // Then
        assertThat(avgRating).isNotNull();
        assertThat(avgRating).isEqualTo((9.0 + 7.0 + 10.0) / 3.0);
    }

    @Test
    @DisplayName("Should get rating distribution by media type")
    void shouldGetRatingDistributionByMediaType() {
        // When
        List<Object[]> distribution = repository.getRatingDistributionByMediaType(testUserId);
        
        // Then
        assertThat(distribution).hasSize(3);  // MOVIE, BOOK, GAME
    }

    @Test
    @DisplayName("Should get rating distribution by score")
    void shouldGetRatingDistributionByScore() {
        // When
        List<Object[]> distribution = repository.getRatingDistributionByScore(testUserId);
        
        // Then
        assertThat(distribution).isNotEmpty();
        // Each score should appear once
        assertThat(distribution).hasSizeLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Should find recent public ratings for activity feed")
    void shouldFindRecentPublicRatingsForActivityFeed() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        
        // When
        List<Rating> recentRatings = repository.findRecentByUserIdsPublic(
            List.of(testUserId), 
            since
        );
        
        // Then
        assertThat(recentRatings).hasSize(2);  // Only PUBLIC ratings
        assertThat(recentRatings).extracting(Rating::getVisibility)
            .containsOnly(Rating.Visibility.PUBLIC);
    }

    @Test
    @DisplayName("Should delete rating")
    void shouldDeleteRating() {
        // When
        repository.deleteByUserIdAndMediaIdAndMediaType(testUserId, "movie1", MediaType.MOVIE);
        
        // Then
        assertThat(repository.countByUserId(testUserId)).isEqualTo(2);
        assertThat(repository.existsByUserIdAndMediaIdAndMediaType(
            testUserId, "movie1", MediaType.MOVIE
        )).isFalse();
    }
}
