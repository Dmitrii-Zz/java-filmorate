package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class ReviewDBStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review save(Review review) {

        String sqlQuery = "INSERT INTO REVIEWS (REVIEW_CONTENT, IS_POSITIVE, USER_ID, FILM_ID) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        if (review.getUserId() > 0 && review.getFilmId() > 0) {
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
                preparedStatement.setString(1, review.getContent());
                preparedStatement.setBoolean(2, review.getIsPositive());
                preparedStatement.setInt(3, review.getUserId());
                preparedStatement.setInt(4, review.getFilmId());
                return preparedStatement;
            }, keyHolder);

            review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        } else if (review.getUserId() < 0) {
            throw new UserNotFoundException("User with id < 0 does not exist");
        } else if (review.getFilmId() < 0) {
            throw new FilmNotFoundException("Film with id < 0 does not exist");
        }

        return review;
    }

    @Override
    public Review update(Review review) {

        String sqlQuery = "UPDATE REVIEWS SET REVIEW_CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";

        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReviewByID(int reviewId) {

        String sqlQuery = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";

        try {
            jdbcTemplate.update(sqlQuery, reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new ReviewNotFoundException("Review with id= " + reviewId + " have not been found.");
        }
    }

    public Review getReviewById(int reviewId) {

        String sqlQuery = "SELECT REVIEW_ID, REVIEW_CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL FROM REVIEWS WHERE REVIEW_ID = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new ReviewNotFoundException("Review with id = " + reviewId + " have not been found.");
        }

    }

    @Override
    public List<Review> getAllReviewsByFilmId(int filmId, int count) {

        List<Review> reviews;
        String sqlQuery;

        if (filmId == 0) {
            sqlQuery = "SELECT REVIEW_ID, REVIEW_CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL FROM REVIEWS ORDER BY USEFUL DESC LIMIT " + count;
        } else {
            sqlQuery = "SELECT REVIEW_ID, REVIEW_CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL FROM REVIEWS WHERE FILM_ID = " + filmId + " ORDER BY USEFUL DESC LIMIT " + count;
        }

        reviews = new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToReview));

        return reviews;
    }

    @Override
    public Integer getUsefulCount(int reviewId) {

        Review review = getReviewById(reviewId);

        if (review != null) {
            return review.getUseful();
        } else {
            throw new ReviewNotFoundException("Review with id= " + reviewId + " have not been found.");
        }
    }

    @Override
    public void addLikeToReview(int reviewId, int userId) {

        String sqlInsert = "INSERT INTO REVIEWS_LIKES (REVIEW_ID, USER_ID, IS_POSITIVE) VALUES (?, ?, 'true')";
        String sqlUpdateUseful = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";

        jdbcTemplate.update(sqlInsert, reviewId, userId);
        jdbcTemplate.update(sqlUpdateUseful, reviewId);

    }

    @Override
    public void disLikeReview(int reviewId, int userId) {

        String sqlInsert = "INSERT INTO REVIEWS_LIKES (REVIEW_ID, USER_ID, IS_POSITIVE) VALUES (?, ?, 'false')";
        String sqlUpdateUseful = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";

        jdbcTemplate.update(sqlInsert, reviewId, userId);
        jdbcTemplate.update(sqlUpdateUseful, reviewId);

    }

    @Override
    public void deleteReviewLike(int reviewId, int userId) {

        String sqlInsert = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
        String sqlUpdateUseful = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";

        jdbcTemplate.update(sqlInsert, reviewId, userId);
        jdbcTemplate.update(sqlUpdateUseful, reviewId);

    }

    @Override
    public void deleteReviewDisLike(int reviewId, int userId) {

        String sqlInsert = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
        String sqlUpdateUseful = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";

        jdbcTemplate.update(sqlInsert, reviewId, userId);
        jdbcTemplate.update(sqlUpdateUseful, reviewId);

    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review
                .builder()
                .reviewId(rs.getInt("REVIEW_ID"))
                .content(rs.getString("REVIEW_CONTENT"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .userId(rs.getInt("USER_ID"))
                .filmId(rs.getInt("FILM_ID"))
                .useful(rs.getInt("USEFUL"))
                .build();
    }

}
