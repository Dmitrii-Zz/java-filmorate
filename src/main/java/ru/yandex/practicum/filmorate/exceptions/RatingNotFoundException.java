package ru.yandex.practicum.filmorate.exceptions;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(final String message) {
        super(message);
    }
}
