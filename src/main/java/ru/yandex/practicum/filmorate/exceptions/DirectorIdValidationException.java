package ru.yandex.practicum.filmorate.exceptions;

public class DirectorIdValidationException extends RuntimeException {
    public DirectorIdValidationException(final String message) {
        super(message);
    }
}
