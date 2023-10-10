package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    int id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    @NotBlank
    String description;
    @NotNull
    LocalDate releaseDate;
    int duration;
    Mpa mpa;
    @JsonIgnore
    Set<Integer> likes;
    Set<Genre> genres;
    Integer rate;
    Set<Director> director;
}