package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Director {
    private int id;
    @NotBlank
    private String name;

    public Director(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
