package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@Builder
public class Genre {
    @Min(1)
    private int id;

    private String name;

    public Genre(int id) {
        this.id = id;
    }
}
