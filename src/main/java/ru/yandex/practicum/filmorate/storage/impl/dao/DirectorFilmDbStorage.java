package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorFilmStorage;

@Component
@RequiredArgsConstructor
public class DirectorFilmDbStorage implements DirectorFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilmByDirector(int idFilm, int idDirector) {
        String sqlRequest = "MERGE INTO director_film KEY (film_id, director_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlRequest, idFilm, idDirector);
    }

    @Override
    public void deleteDirectorsByFilmId(int id) {
        String sqlRequest = "DELETE FROM director_film WHERE film_id = ?;";
        jdbcTemplate.update(sqlRequest, id);
    }

    @Override
    public void deleteDirectorFilm(int id) {
        String sqlRequest = "DELETE FROM director_film WHERE director_id = ?;";
        jdbcTemplate.update(sqlRequest, id);
    }
}