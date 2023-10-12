package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    private Integer reviewId;

    @NotNull
    private String content;

    private Boolean isPositive;

    private Integer userId;

    private Integer filmId;

    private Integer useful;

}
