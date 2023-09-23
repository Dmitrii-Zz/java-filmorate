package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.storage.impl.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RatingServiceTest {
    private final RatingStorage ratingStorage = new MpaDbStorage(new JdbcTemplate());
    private final RatingService ratingService = new RatingService(ratingStorage);

    @Test
    public void getPopularFilmsWrongCountTest() {
        final RatingNotFoundException exception = assertThrows(
                RatingNotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        ratingService.findRatingById(7);
                    }
                });

        assertEquals("Рейтинг 7 не существует.", exception.getMessage());
    }
}
