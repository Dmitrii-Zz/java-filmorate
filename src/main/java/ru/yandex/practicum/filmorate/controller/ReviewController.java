package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review save(@Valid @RequestBody Review review) {
        log.info("Invoke save method with review = {}", review);
        return reviewService.save(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Invoke update method with review = {}", review);
        return reviewService.update(review);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReviewByID(@PathVariable("reviewId") int reviewId) {
        log.info("Invoke delete method with id = " + reviewId);
        reviewService.deleteReviewByID(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable("reviewId") int reviewId) {
        log.info("Invoke getReviewById method to return review by Id = " + reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getAllReviewsByFilmId(@RequestParam(defaultValue = "0") int filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        log.info("Invoke getAllReviewsByFilmId method to return reviews by filmId = "
                + filmId + " and count = " + count);
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLikeToReview(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Invoke addLikeToReview method to review  with id = " + id + " and userId " + userId);
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void disLikeReview(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Invoke disLikeReview method to review with id = " + id + " and userId " + userId);
        reviewService.disLikeReview(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteReviewLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Invoke deleteReviewLike method to review with id = " + id + " and userId " + userId);
        reviewService.deleteReviewLike(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteReviewDisLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Invoke deleteReviewDisLike method to review for filmId = " + id + " and userId " + userId);
        reviewService.deleteReviewDisLike(id, userId);
    }


}
