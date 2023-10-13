package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorFilmStorage;

@Component
@RequiredArgsConstructor
public class DirectorFilmDbStorage implements DirectorFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilmByDirector(int idFilm, int idDirector) {
        String sqlRequest =
                String.format("MERGE INTO director_film KEY (film_id, director_id) VALUES (%d, %d);",
                        idFilm, idDirector);
        jdbcTemplate.execute(sqlRequest);
    }

    @Override
    public void deleteDirectorsByFilmId(int id) {
        String sqlRequest =
                String.format("DELETE FROM director_film WHERE film_id = %d;", id);
        jdbcTemplate.execute(sqlRequest);
    }

    @Override
    public void deleteDirectorFilm(int id) {
        jdbcTemplate.execute(String.format("DELETE FROM director_film WHERE director_id = %d;", id));
    }
}
