package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    int id;

    @NotNull(message = "Отсутствует название фильма.")
    @NotBlank(message = "Название фильма не должно быть пустым.")
    String name;

    @NotNull(message = "Отсутствует описание фильма.")
    @NotBlank(message = "Описание фильма не должно быть пустым.")
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов.")
    String description;

    @NotNull(message = "Отсутствует дата релиза фильма.")
    LocalDate releaseDate;

    @Min(1)
    int duration;

    @JsonIgnore
    Set<Integer> likes;

    Mpa mpa;

    Set<Genre> genres;

    Set<Director> directors;

    Integer rate;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}