package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.model.Director;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@Sql({"/schema.sql", "/data.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AddDirectorTest {
    private final DirectorController directorController;

    @Test
    public void directorTest() {
        log.info("Тест создания режиссера");
        Director director = Director.builder()
                .name("Director one")
                .build();

        Director saveDirector = directorController.createDirector(director);

        assertAll("Проверка режиссера",
                () -> assertEquals(1, saveDirector.getId()),
                () -> assertEquals("Director one", saveDirector.getName()));

        log.info("Тест возврата режиссера по идентификатору");

        Director directorById = directorController.getDirectorById(1);
        assertAll("Проверка режиссера",
                () -> assertEquals(1, saveDirector.getId()),
                () -> assertEquals("Director one", saveDirector.getName()));

        log.info("Обновление режиссера");
        Director updateDirector = Director.builder().name("Update").id(1).build();
        Director updateD = directorController.updateDirector(updateDirector);

        assertAll("Проверка обновлений",
                () -> assertEquals(1, updateDirector.getId()),
                () -> assertEquals("Update", updateDirector.getName()));

        log.info("Тест создания второго режиссера");
        Director directorTwo = Director.builder()
                .name("Director two")
                .build();

        Director saveDirectorTwo = directorController.createDirector(directorTwo);

        assertAll("Проверка второго режиссера",
                () -> assertEquals(2, saveDirectorTwo.getId()),
                () -> assertEquals("Director two", saveDirectorTwo.getName()));


        log.info("Тест возврата списка режиссеров");
        assertEquals(2, directorController.findAll().size());

        log.info("Удаление режиссера");
        directorController.deleteDirector(1);
        assertEquals(1, directorController.findAll().size());
    }
}
