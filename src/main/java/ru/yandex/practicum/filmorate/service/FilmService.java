package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private Map<Integer, Film> films = new HashMap<>();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        pullFromStorage();
        if (films.values().stream().anyMatch(f -> f.getName().equals(film.getName()))) {
            log.warn("Фильм с {} уже существует", film.getName());
            throw new FilmAlreadyExistException("Фильм с " + film.getName() + " уже существует");
        }
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        pullFromStorage();
        checkFilmForExists(film.getId());
        return filmStorage.update(film);
    }

    public Film getFilmById(int id) {
        pullFromStorage();
        checkFilmForExists(id);
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getAllFilmList() {
        return filmStorage.getAllObjList();
    }

    public int clearFilms() {
        return filmStorage.clearAll();
    }

    public Film deleteFilm(int filmId) {
        pullFromStorage();
        checkFilmForExists(filmId);

        Film film = filmStorage.getFilmById(filmId);

        return filmStorage.delete(film);
    }

    public Film updateLikeToFilm(int filmId, int userId, boolean isAddLike) {
        checkFilmForExists(filmId);
        checkUserForExists(userId);

        Film film = filmStorage.getFilmById(filmId);
        if (isAddLike) {
            film.getUserLikeIds().add(userId);
            log.debug("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        } else {
            film.getUserLikeIds().remove(userId);
            log.debug("Пользователь id={} убрал лайк фильму id={}", userId, filmId);
        }
        return filmStorage.update(film);
    }

    public int getFilmLikesCount(int filmId) {
        checkFilmForExists(filmId);
        Film film = filmStorage.getFilmById(filmId);
        int count = film.getUserLikeIds().size();
        log.debug("Лайков у фильма id={} - {}", filmId, count);
        return count;
    }

    public List<Film> getPopularFilms(int count) {
        pullFromStorage();
        List<Film> filmsList = new ArrayList<>(films.values());
        Comparator<Film> comparator = Comparator.comparing(f->f.getUserLikeIds().size());
        filmsList.sort(comparator.reversed());
        log.debug("Запрошено {} фильмов", count);

        return filmsList.stream().limit(count).collect(Collectors.toList());
    }

    private void checkUserForExists(int id) {
        Map<Integer, User> users = userStorage.getUsersMap();
        if (!users.containsKey(id)) {
            log.warn("Пользователя с id={} не существует", id);
            throw new UserNotFoundException(
                    "Пользователя с id=" + id + " не существует");
        }
    }

    private void checkFilmForExists(int id) {
        if (!films.containsKey(id)) {
            log.warn("Фильм с id {} не существует", id);
            throw new FilmNotFoundException(
                    "Фильма с id " + id + " не существует");
        }
    }

    private void pullFromStorage() {
        films = filmStorage.getFilmsMap();
    }
}
