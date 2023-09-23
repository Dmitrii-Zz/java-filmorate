package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.impl.dao.GenreDbStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenreServiceTest {
    private final GenreService genreService = new GenreService(new GenreDbStorage(new JdbcTemplate()));
    @Test
    public void getPopularFilmsWrongCountTest() {
        final GenreNotFoundException exception = assertThrows(
                GenreNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        genreService.findGenreById(7);
                    }
                });

        assertEquals("Жанр с id = '7' отсутствует", exception.getMessage());
    }
}
