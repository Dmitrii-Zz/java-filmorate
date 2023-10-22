package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    int id;

    @NotNull(message = "Отсутствует электронная почта.")
    @NotBlank(message = "Электронная почта не должна быть пустой.")
    @Email(message = "Введен некорректный адрес электронный почты.")
    String email;

    @NotNull(message = "Отсутствует логин.")
    @NotBlank(message = "Логин не должен быть пустым.")
    String login;

    String name;

    @NotNull(message = "Отсутствует дата рождения пользователя.")
    @Past
    LocalDate birthday;

    Set<Integer> friends;

    @JsonIgnore
    Map<Integer, Boolean> friendshipStatus;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}