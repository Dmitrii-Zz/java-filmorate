package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review save(Review review);

    Review update(Review review);

    void deleteReviewByID(int id);

    Review getReviewById(int reviewId);

    List<Review> getAllReviewsByFilmId(int filmId, int count);

    Integer getUsefulCount(int reviewId);

    void addLikeToReview(int reviewId, int userId);

    void disLikeReview(int reviewId, int userId);

    void deleteReviewLike(int reviewId, int userId);

    void deleteReviewDisLike(int reviewId, int userId);

}
