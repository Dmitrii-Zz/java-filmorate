package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FeedStorage feedStorage;

    public Review save(Review review) {
        Review result = reviewStorage.save(review);
        Feed feed = new Feed();
        feed.setTimestamp(Instant.now().toEpochMilli());
        feed.setUserId(result.getUserId());
        feed.setEntityId(result.getReviewId());
        feed.setEventType("REVIEW");
        feed.setOperation("ADD");
        feedStorage.addFeed(feed);
        return result;
    }

    public Review update(Review review) {
        Review result = reviewStorage.update(review);
        Feed feed = new Feed();
        feed.setTimestamp(Instant.now().toEpochMilli());
        feed.setUserId(result.getUserId());
        feed.setEntityId(result.getReviewId());
        feed.setEventType("REVIEW");
        feed.setOperation("UPDATE");
        feedStorage.addFeed(feed);
        return result;
    }

    public void deleteReviewByID(int reviewId) {
        Review result = getReviewById(reviewId);
        reviewStorage.deleteReviewByID(reviewId);
        Feed feed = new Feed();
        feed.setTimestamp(Instant.now().toEpochMilli());
        feed.setUserId(result.getUserId());
        feed.setEntityId(result.getReviewId());
        feed.setEventType("REVIEW");
        feed.setOperation("REMOVE");
        feedStorage.addFeed(feed);
    }

    public Review getReviewById(int reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getAllReviewsByFilmId(int filmId, int count) {
        return reviewStorage.getAllReviewsByFilmId(filmId, count);
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
