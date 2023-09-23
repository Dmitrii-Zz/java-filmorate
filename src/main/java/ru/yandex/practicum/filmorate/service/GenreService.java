package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreRepository;

    public List<Genre> findAllGenres() {
        return genreRepository.findAllGenres();
    }

    public Genre findGenreById(int id) {
        if (id < 1 || id > 6) {
            throw new GenreNotFoundException(String.format("Жанр с id = '%d' отсутствует", id));
        }

        return genreRepository.findGenreById(id);
    }
}
