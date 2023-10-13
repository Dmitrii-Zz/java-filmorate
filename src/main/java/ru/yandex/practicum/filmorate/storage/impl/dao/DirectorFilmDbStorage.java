package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorFilmStorage;

@Component
@RequiredArgsConstructor
public class DirectorFilmDbStorage implements DirectorFilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilmByDirector(int idFilm, int idDirector) {
//        String sqlRequest =
//                String.format("MERGE INTO director_film KEY (film_id, director_id) VALUES (%d, %d);",
//                idFilm, idDirector);
//        jdbcTemplate.execute(sqlRequest);

        jdbcTemplate.update("insert into director_film (film_id, director_id) values (?, ?)",
                idFilm, idDirector);
    }
}
