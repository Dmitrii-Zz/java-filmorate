package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    @Min(1)
    private int id;

    private String name;
}
