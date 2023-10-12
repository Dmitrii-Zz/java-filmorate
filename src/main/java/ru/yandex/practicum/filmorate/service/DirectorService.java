package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DirectorIdValidationException;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.impl.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.Constants.MIN_DIRECTOR_ID;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        validationDirectorId(director.getId());
        return directorStorage.createDirector(director);
    }

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director getDirectorById(int id) {
        validationDirectorId(id);
        directorStorage.findDirectorById(id);
        return directorStorage.getDirectorById(id);
    }

    public Director updateDirector(Director director) {
        findDirectorById(director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
      //  findDirectorById(id);
        directorStorage.deleteDirector(id);
    }

    private void validationDirectorId(int id) {
        if (id < MIN_DIRECTOR_ID) {
            throw new DirectorIdValidationException(String.format("Неверный идентификатор режиссера id = '%d'", id));
        }
    }

    private void findDirectorById(int id) {
        validationDirectorId(id);
        if (!directorStorage.findDirectorById(id)) {
            throw new DirectorNotFoundException(String.format("Режиссер с id = '%d' не найден", id));
        }
    }
}
