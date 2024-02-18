package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
@Slf4j
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public List<Mpa> findAllRating() {
        log.info("Запрос на возврат списка рейтингов.");
        return ratingService.findAllRating();
    }

    @GetMapping("/{id}")
    public Mpa findRatingById(@PathVariable int id) {
        log.info("Запрос на возврат рейтинга id = " + id);
        return ratingService.findRatingById(id);
    }
}
