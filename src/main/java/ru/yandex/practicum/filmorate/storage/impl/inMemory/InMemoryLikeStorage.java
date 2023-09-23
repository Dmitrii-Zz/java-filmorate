package ru.yandex.practicum.filmorate.storage.impl.inMemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeFilmsStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InMemoryLikeStorage implements LikeFilmsStorage {
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Override
    public void addLike(int filmId, int userId) {
        Set<Integer> filmLikes = likes.getOrDefault(filmId, new HashSet<>());
        filmLikes.add(userId);
        likes.put(filmId, filmLikes);
        inMemoryFilmStorage.getFilmById(filmId).setLikes(filmLikes);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Set<Integer> filmLikes = likes.get(filmId);
        filmLikes.remove(userId);
        likes.put(filmId, filmLikes);
        inMemoryFilmStorage.getFilmById(filmId).setLikes(filmLikes);
    }

    @Override
    public Set<Integer> getAllLikeFilmById(int filmId) {
        return likes.get(filmId);
    }
}
