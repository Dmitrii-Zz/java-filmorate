package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Director {

    @Min(1)
    private int id;

    @NotBlank(message = "Имя не должно быть пустым.")
    private String name;
}
