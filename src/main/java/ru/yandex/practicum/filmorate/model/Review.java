package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {

    Integer reviewId;

    String content;

    Boolean isPositive;

    @NotNull
    Integer userId;

    @NotNull
    Integer filmId;

    Integer useful;

}
