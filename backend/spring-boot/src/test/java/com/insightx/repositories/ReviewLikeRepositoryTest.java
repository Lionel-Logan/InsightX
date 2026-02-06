package com.insightx.repositories;

import com.insightx.entities.ReviewLike;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ReviewLikeRepository Tests")
class ReviewLikeRepositoryTest {

    @Autowired
    private ReviewLikeRepository repository;

    private UUID testReviewId;
    private UUID testUserId1;
    private UUID testUserId2;

    @BeforeEach
    void setUp() {
        testReviewId = UUID.randomUUID();
        testUserId1 = UUID.randomUUID();
        testUserId2 = UUID.randomUUID();
        
        // User1 likes the review
        ReviewLike like1 = ReviewLike.builder()
            .reviewId(testReviewId)
            .userId(testUserId1)
            .build();
        
        // User2 likes the review
        ReviewLike like2 = ReviewLike.builder()
            .reviewId(testReviewId)
            .userId(testUserId2)
            .build();
        
        repository.saveAll(List.of(like1, like2));
    }

    @Test
    @DisplayName("Should check if user has liked review")
    void shouldCheckIfUserHasLikedReview() {
        // When
        boolean hasLiked = repository.existsByReviewIdAndUserId(testReviewId, testUserId1);
        boolean hasNotLiked = repository.existsByReviewIdAndUserId(testReviewId, UUID.randomUUID());
        
        // Then
        assertThat(hasLiked).isTrue();
        assertThat(hasNotLiked).isFalse();
    }

    @Test
    @DisplayName("Should find all users who liked a review")
    void shouldFindAllUsersWhoLikedReview() {
        // When
        List<ReviewLike> likes = repository.findByReviewId(testReviewId);
        
        // Then
        assertThat(likes).hasSize(2);
        assertThat(likes).extracting(ReviewLike::getUserId)
            .containsExactlyInAnyOrder(testUserId1, testUserId2);
    }

    @Test
    @DisplayName("Should count likes for a review")
    void shouldCountLikesForReview() {
        // When
        long count = repository.countByReviewId(testReviewId);
        
        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should count likes by a user")
    void shouldCountLikesByUser() {
        // When
        long count = repository.countByUserId(testUserId1);
        
        // Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Should delete like (unlike)")
    void shouldDeleteLike() {
        // When
        repository.deleteByReviewIdAndUserId(testReviewId, testUserId1);
        
        // Then
        assertThat(repository.existsByReviewIdAndUserId(testReviewId, testUserId1)).isFalse();
        assertThat(repository.countByReviewId(testReviewId)).isEqualTo(1);
    }

    @Test
    @DisplayName("Should get like counts for multiple reviews")
    void shouldGetLikeCountsForMultipleReviews() {
        // Given
        UUID review2Id = UUID.randomUUID();
        repository.save(ReviewLike.builder()
            .reviewId(review2Id)
            .userId(testUserId1)
            .build());
        
        // When
        List<Object[]> likeCounts = repository.getLikeCountsForReviews(
            List.of(testReviewId, review2Id)
        );
        
        // Then
        assertThat(likeCounts).hasSize(2);
    }
}
