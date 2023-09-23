package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface RatingStorage {

    List<Mpa> findAllRating();

    Mpa findRatingById(int id);
}
