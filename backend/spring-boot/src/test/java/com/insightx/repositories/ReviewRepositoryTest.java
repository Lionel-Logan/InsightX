package com.insightx.repositories;

import com.insightx.entities.MediaType;
import com.insightx.entities.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ReviewRepository Tests")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private UUID testUserId;
    private Review review1, review2, review3;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        review1 = Review.builder()
            .userId(testUserId)
            .mediaId("movie1")
            .mediaType(MediaType.MOVIE)
            .reviewText("Amazing movie! Highly recommended.")
            .isSpoiler(false)
            .visibility(Review.Visibility.PUBLIC)
            .upvoteCount(10)
            .build();
        
        review2 = Review.builder()
            .userId(testUserId)
            .mediaId("book1")
            .mediaType(MediaType.BOOK)
            .reviewText("Great book but slow pacing.")
            .isSpoiler(false)
            .visibility(Review.Visibility.PUBLIC)
            .upvoteCount(5)
            .build();
        
        review3 = Review.builder()
            .userId(testUserId)
            .mediaId("game1")
            .mediaType(MediaType.GAME)
            .reviewText("Best RPG ever played!")
            .isSpoiler(true)
            .visibility(Review.Visibility.PRIVATE)
            .upvoteCount(0)
            .build();
        
        repository.saveAll(List.of(review1, review2, review3));
    }

    @Test
    @DisplayName("Should find all reviews by userId")
    void shouldFindAllReviewsByUserId() {
        // When
        List<Review> reviews = repository.findByUserId(testUserId);
        
        // Then
        assertThat(reviews).hasSize(3);
    }

    @Test
    @DisplayName("Should find public reviews for media")
    void shouldFindPublicReviewsForMedia() {
        // When
        List<Review> reviews = repository.findByMediaIdAndMediaTypeAndVisibility(
            "movie1", MediaType.MOVIE, Review.Visibility.PUBLIC
        );
        
        // Then
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getReviewText()).contains("Amazing movie");
    }

    @Test
    @DisplayName("Should find reviews sorted by upvotes")
    void shouldFindReviewsSortedByUpvotes() {
        // Create another public review for movie1
        Review review4 = Review.builder()
            .userId(UUID.randomUUID())
            .mediaId("movie1")
            .mediaType(MediaType.MOVIE)
            .reviewText("Good movie!")
            .visibility(Review.Visibility.PUBLIC)
            .upvoteCount(15)
            .build();
        repository.save(review4);
        
        // When
        Page<Review> reviews = repository.findByMediaIdAndMediaTypeAndVisibilityOrderByUpvoteCountDesc(
            "movie1", MediaType.MOVIE, Review.Visibility.PUBLIC, PageRequest.of(0, 10)
        );
        
        // Then
        assertThat(reviews.getContent()).hasSize(2);
        assertThat(reviews.getContent().get(0).getUpvoteCount()).isEqualTo(15);
        assertThat(reviews.getContent().get(1).getUpvoteCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should increment upvote count")
    void shouldIncrementUpvoteCount() {
        // When
        repository.incrementUpvoteCount(review1.getId());
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Review updated = repository.findById(review1.getId()).orElseThrow();
        assertThat(updated.getUpvoteCount()).isEqualTo(11);
    }

    @Test
    @DisplayName("Should decrement upvote count")
    void shouldDecrementUpvoteCount() {
        // When
        repository.decrementUpvoteCount(review1.getId());
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Review updated = repository.findById(review1.getId()).orElseThrow();
        assertThat(updated.getUpvoteCount()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should not decrement below zero")
    void shouldNotDecrementBelowZero() {
        // When
        repository.decrementUpvoteCount(review3.getId());
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Review updated = repository.findById(review3.getId()).orElseThrow();
        assertThat(updated.getUpvoteCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should find recent public reviews for activity feed")
    void shouldFindRecentPublicReviewsForActivityFeed() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        
        // When
        List<Review> reviews = repository.findRecentByUserIdsPublic(
            List.of(testUserId),
            since
        );
        
        // Then
        assertThat(reviews).hasSize(2);  // Only PUBLIC reviews
        assertThat(reviews).extracting(Review::getVisibility)
            .containsOnly(Review.Visibility.PUBLIC);
    }

    @Test
    @DisplayName("Should count reviews by userId")
    void shouldCountReviewsByUserId() {
        // When
        long count = repository.countByUserId(testUserId);
        
        // Then
        assertThat(count).isEqualTo(3);
    }
}
