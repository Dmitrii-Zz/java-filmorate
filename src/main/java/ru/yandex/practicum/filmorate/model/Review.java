package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    Integer reviewId;
    String content;
    boolean isPositive;
    Integer userId;
    Integer filmId;
    Integer useful;

}
