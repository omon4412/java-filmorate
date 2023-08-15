package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

/**
 * Сервис для работы с жанрами.
 */
@Service
@Slf4j
public class GenreService implements BaseEntityService<Genre> {
    /**
     * Хранилище жанров.
     */
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public int clearEntities() {
        log.debug("Начато удаление всех жанров");
        return genreStorage.clearAll();
    }

    @Override
    public Genre deleteEntity(int genreId) {
        checkUserForExists(genreId);
        log.debug("Начато удаление жанра id=" + genreId);
        return genreStorage.delete(genreId);
    }

    @Override
    public Genre getEntityById(int genreId) {
        checkUserForExists(genreId);
        return genreStorage.getGenreById(genreId);
    }

    @Override
    public Collection<Genre> getAllEntities() {
        return genreStorage.getAllObjList();
    }

    @Override
    public Genre addEntity(Genre genre) {
        log.debug("Начато добаление жанра - " + genre);
        return genreStorage.add(genre);
    }

    @Override
    public Genre updateEntity(Genre genre) {
        checkUserForExists(genre.getId());
        log.debug("Начато обновдение жанра - " + genre);
        return genreStorage.update(genre);
    }

    private void checkUserForExists(int genreId) {
        if (!genreStorage.checkForExists(genreId)) {
            log.error("Жанр с id={} не существует", genreId);
            throw new GenreNotFoundException(
                    "Жанр с id=" + genreId + " не существует");
        }
    }
}
