package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Mpa {
    @Min(1)
    private int id;

    private String name;
}
