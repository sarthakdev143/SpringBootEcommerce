package com.example.web_app.rest;

import com.example.web_app.model.ReviewDTO;
import com.example.web_app.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReviewResource {

    private final ReviewService reviewService;

    public ReviewResource(final ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReview(
            @PathVariable(name = "reviewId") final Integer reviewId) {
        return ResponseEntity.ok(reviewService.get(reviewId));
    }

    @PostMapping
    public ResponseEntity<Integer> createReview(@RequestBody @Valid final ReviewDTO reviewDTO) {
        final Integer createdReviewId = reviewService.create(reviewDTO);
        return new ResponseEntity<>(createdReviewId, HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Integer> updateReview(
            @PathVariable(name = "reviewId") final Integer reviewId,
            @RequestBody @Valid final ReviewDTO reviewDTO) {
        reviewService.update(reviewId, reviewDTO);
        return ResponseEntity.ok(reviewId);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable(name = "reviewId") final Integer reviewId) {
        reviewService.delete(reviewId);
        return ResponseEntity.noContent().build();
    }

}
