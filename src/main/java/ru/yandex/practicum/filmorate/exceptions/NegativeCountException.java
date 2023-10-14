package ru.yandex.practicum.filmorate.exceptions;

public class NegativeCountException extends RuntimeException {
    public NegativeCountException(final String message) {
        super(message);
    }
}
