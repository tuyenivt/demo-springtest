package com.coloza.demo.springtest.repository;

import com.coloza.demo.springtest.AbstractMongoIT;
import com.coloza.demo.springtest.model.Review;
import com.coloza.demo.springtest.model.ReviewEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

class ReviewRepositoryTest extends AbstractMongoIT {

    @Autowired
    private ReviewRepository repository;

    @BeforeEach
    void beforeEach() {
        loadData("/data/review/sample.json", "Reviews");
    }

    @Test
    void testFindAll() {
        var reviews = repository.findAll();
        Assertions.assertEquals(2, reviews.size(), "Should be two reviews in the database");
    }

    @Test
    void testFindByIdSuccess() {
        var review = repository.findById("1");
        Assertions.assertTrue(review.isPresent(), "We should have found a review with ID 1");
        review.ifPresent(r -> {
            Assertions.assertEquals("1", r.getId(), "Review ID should be 1");
            Assertions.assertEquals(1, r.getProductId().intValue(), "Review Product ID should be 1");
            Assertions.assertEquals(1, r.getVersion().intValue(), "Review version should be 1");
            Assertions.assertEquals(1, r.getEntries().size(), "Review 1 should have one entry");
        });
    }

    @Test
    void testFindByIdFailure() {
        var review = repository.findById("99");
        Assertions.assertFalse(review.isPresent(), "We should not find a review with ID 99");
    }

    @Test
    void testFindByProductIdSuccess() {
        var review = repository.findByProductId(1);
        Assertions.assertTrue(review.isPresent(), "There should be a review for product ID 1");
    }

    @Test
    void testFindByProductIdFailure() {
        var review = repository.findByProductId(99);
        Assertions.assertFalse(review.isPresent(), "There should not be a review for product ID 99");
    }

    @Test
    void testSave() {
        // Create a test Review
        var reviewEntry = new ReviewEntry("test-user", new Date(), "This is a review");
        var review = Review.builder().productId(10).version(1).entries(List.of(reviewEntry)).build();

        // Persist the review to MongoDB
        var savedReview = repository.save(review);

        // Retrieve the review
        var loadedReview = repository.findById(savedReview.getId());

        // Validations
        Assertions.assertTrue(loadedReview.isPresent());
        loadedReview.ifPresent(r -> {
            Assertions.assertEquals(10, r.getProductId().intValue());
            Assertions.assertEquals(1, r.getVersion().intValue(), "Review version should be 1");
            Assertions.assertEquals(1, r.getEntries().size(), "Review 1 should have one entry");
        });
    }

    @Test
    void testUpdate() {
        // Retrieve review 2
        var review = repository.findById("2");
        Assertions.assertTrue(review.isPresent(), "Review 2 should be present");
        Assertions.assertEquals(3, review.get().getEntries().size(), "There should be 3 review entries");

        // Add an entry to the review and save
        var reviewToUpdate = review.get();
        reviewToUpdate.getEntries().add(new ReviewEntry("test-user-2", new Date(), "This is a fourth review"));
        repository.save(reviewToUpdate);

        // Retrieve the review again and validate that it now has 4 entries
        var updatedReview = repository.findById("2");
        Assertions.assertTrue(updatedReview.isPresent(), "Review 2 should be present");
        Assertions.assertEquals(4, updatedReview.get().getEntries().size(), "There should be 3 review entries");
    }

    @Test
    void testDelete() {
        // Delete review 2
        repository.deleteById("2");

        // Confirm that it is no longer in the database
        var review = repository.findById("2");
        Assertions.assertFalse(review.isPresent(), "Review 2 should now be deleted from the database");
    }
}
