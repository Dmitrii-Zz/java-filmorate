package ru.yandex.practicum.filmorate.storage.interfaces;

public interface DirectorFilmStorage {

    void addFilmByDirector(int idFilm, int idDirector);

    void deleteDirectorFilm(int id);

    void deleteDirectorsByFilmId(int id);
}
