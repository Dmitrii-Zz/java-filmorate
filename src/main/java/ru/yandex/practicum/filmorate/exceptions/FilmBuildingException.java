package ru.yandex.practicum.filmorate.exceptions;

public class FilmBuildingException extends RuntimeException {
    
    public FilmBuildingException (final String message) {
        super("Ошибка построения объекта Film: " + message);
    }
}
