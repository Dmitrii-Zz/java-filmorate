package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    public Review save(Review review) {
        return reviewStorage.save(review);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public void deleteReviewByID(int reviewId) {
        reviewStorage.deleteReviewByID(reviewId);
    }

    public Review getReviewById(int reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getAllReviewsByFilmId(int filmId, int count) {
        return reviewStorage.getAllReviewsByFilmId(filmId,count);
    }

    public void addLikeToReview(int reviewId, int userId) {
        reviewStorage.addLikeToReview(reviewId, userId);
    }

    public void disLikeReview(int reviewId, int userId) {
        reviewStorage.disLikeReview(reviewId, userId);
    }

    public void deleteReviewLike(int reviewId, int userId) {
        reviewStorage.deleteReviewLike(reviewId, userId);
    }

    public void deleteReviewDisLike(int reviewId, int userId) {
        reviewStorage.deleteReviewDisLike(reviewId, userId);
    }

}
