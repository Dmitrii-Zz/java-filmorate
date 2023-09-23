package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.Set;

public interface LikeFilmsStorage {

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    Set<Integer> getAllLikeFilmById(int filmId);
}
