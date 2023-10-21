package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> findAll() {
        log.info("Запрос на возврат всех режиссеров.");
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("Запрос на возврат режиссера с id = " + id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Запрос на создание режиссера.");
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Обновление режиссера с id = " + director.getId());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        log.info("Удаление режиссера с id = " + id);
        directorService.deleteDirector(id);
    }
}
