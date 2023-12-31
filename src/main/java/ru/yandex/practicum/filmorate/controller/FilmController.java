package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable int filmId) {
        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> popularFilms(@RequestParam(defaultValue = "10") Integer count, @RequestParam(defaultValue = "0") Integer genreId, @RequestParam(defaultValue = "0") Integer year) {
        return filmService.popularFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam(required = false) String query, @RequestParam(required = false) List<String> by) {
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        return filmService.commonFilms(userId, friendId);
    }
}