package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
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
    String name;
    @NotNull
    LocalDate birthday;
    Set<Integer> friends;
}
