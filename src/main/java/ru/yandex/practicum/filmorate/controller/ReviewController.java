package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review save(Review review) {
        log.info("Invoke save method with review = {}", review);
        return reviewService.save(review);
    }

    @PutMapping
    public Review update(Review review) {
        log.info("Invoke update method with review = {}", review);
        return reviewService.update(review);
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReviewByID(int id) {
        log.info("Invoke delete method with id = " + id);
        reviewService.deleteReviewByID(id);
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(int id) {
        log.info("Invoke getReviewById method to return review by Id = " + id);
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReviewsByFilmId(@RequestParam(defaultValue = "0") int filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        log.info("Invoke getAllReviewsByFilmId method to return reviews by filmId = " + filmId + " and count = " + count);
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLikeToReview(int id, int userId) {
        log.info("Invoke addLikeToReview method to review  with id = " + id + " and userId " + userId);
        reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void disLikeReview(int id, int userId) {
        log.info("Invoke disLikeReview method to review with id = " + id + " and userId " + userId);
        reviewService.disLikeReview(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteReviewLike(int id, int userId) {
        log.info("Invoke deleteReviewLike method to review with id = " + id + " and userId " + userId);
        reviewService.deleteReviewLike(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteReviewDisLike(int id, int userId) {
        log.info("Invoke deleteReviewDisLike method to review for filmId = " + id + " and userId " + userId);
        reviewService.deleteReviewDisLike(id, userId);
    }


}
