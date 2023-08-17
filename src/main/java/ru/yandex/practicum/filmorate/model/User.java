package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    int id;
    @Email
    @NotBlank
    @NotNull
    String email;
    @NotBlank
    @NotNull
    String login;
    private String name;
    @NotNull
    LocalDate birthday;
}
