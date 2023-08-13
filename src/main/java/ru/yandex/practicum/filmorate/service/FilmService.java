package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с фильмами
 */
@Service
@Slf4j
public class FilmService {
    /**
     * Хранилище фильмов
     */
    private final FilmStorage filmStorage;
    /**
     * Хранилище пользователей
     */
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    /**
     * Добавляет фильм в хранилище
     *
     * @param film Фильм {@link Film}
     * @return Добавленный фильм
     * @throws FilmAlreadyExistException Если фильм уже существует
     */
    @Transactional
    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    /**
     * Обновляет фильм в хранилище
     *
     * @param film Фильм {@link Film}
     * @return Обновлённый фильм
     */
    public Film updateFilm(Film film) {
        checkFilmForExists(film.getId());
        return filmStorage.update(film);
    }

    /**
     * Удаляет фильм из хранилища
     *
     * @param filmId id фильма
     * @return Удалённый фильм
     */
    public Film deleteFilm(int filmId) {
        checkFilmForExists(filmId);
        return filmStorage.delete(filmId);
    }

    /**
     * Возвращает фильм по его id
     *
     * @param id id фильма
     * @return Найденный фильм
     */
    public Film getFilmById(int id) {
        checkFilmForExists(id);
        return filmStorage.getFilmById(id);
    }

    /**
     * Возвращает список всех фильмов
     *
     * @return Список всех фильмов
     */
    public Collection<Film> getAllFilmList() {
        return filmStorage.getAllObjList();
    }

    /**
     * Удаляет все фильмы из хранилища
     *
     * @return Количество удалённых фильмов
     */
    public int clearFilms() {
        return filmStorage.clearAll();
    }

    /**
     * Обновляет лайк у фильма от пользователя
     *
     * @param filmId    id фильма
     * @param userId    id пользователя
     * @param isAddLike Флаг, указывающий, добавить или удалить лайк
     * @return Обновлённый фильм
     */
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

    /**
     * Возвращает количество лайков у фильма
     *
     * @param filmId id фильма
     * @return Количество лайков
     */
    public int getFilmLikesCount(int filmId) {
        checkFilmForExists(filmId);
        Film film = filmStorage.getFilmById(filmId);
        int count = film.getUserLikeIds().size();
        log.debug("Лайков у фильма id={} - {}", filmId, count);
        return count;
    }

    /**
     * Возврящает список популярных фильмов
     * в порядке убывания лайков
     *
     * @param count Количество фильмов в списке
     * @return Список популярных фильмов
     */
    public List<Film> getPopularFilms(int count) {
        //pullFromStorage();
//        List<Film> filmsList = new ArrayList<>(films.values());
//        Comparator<Film> comparator = Comparator.comparing(f -> f.getUserLikeIds().size());
//        filmsList.sort(comparator.reversed());
//        log.debug("Запрошено {} фильмов", count);
//
//        return filmsList.stream()
//                .limit(count)
//                .collect(Collectors.toList());
        return null;
    }

    /**
     * Проверка на существование пользователя
     *
     * @param id id Пользователя
     * @throws UserNotFoundException Если пользователь не нейден
     */
    private void checkUserForExists(int id) throws UserNotFoundException {
        Map<Integer, User> users = userStorage.getUsersMap();
        if (!users.containsKey(id)) {
            log.error("Пользователя с id={} не существует", id);
            throw new UserNotFoundException(
                    "Пользователя с id=" + id + " не существует");
        }
    }

    /**
     * Проверка на существование пользователя
     *
     * @param id id Пользователя
     * @throws UserNotFoundException Если пользователь не найден
     */
    private void checkFilmForExists(int id) throws FilmNotFoundException {
        if (!filmStorage.checkForExists(id)) {
            log.error("Фильма с id={} не существует", id);
            throw new FilmNotFoundException(
                    "Фильма с id=" + id + " не существует");
        }
    }
}
