package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingRepository;

    public List<Mpa> findAllRating() {
        return ratingRepository.findAllRating();
    }

    public Mpa findRatingById(int id) {
        if (id < 1 || id > 5) {
            throw new RatingNotFoundException(String.format("Рейтинг %d не существует.", id));
        }

        return ratingRepository.findRatingById(id);
    }
}
