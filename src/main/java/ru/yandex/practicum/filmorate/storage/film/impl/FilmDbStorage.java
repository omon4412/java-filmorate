package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.impl.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.util.Constants;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Хранилище для фильмов в бд
 */
@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final MpaRatingDbStorage mpaRatingDbStorage;

    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         MpaRatingDbStorage mpaRatingDbStorage,
                         GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRatingDbStorage = mpaRatingDbStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film add(Film film) {
        String query = "INSERT INTO \"film\" (\"name\"," +
                " \"description\"," +
                " \"release_date\"," +
                " \"duration\"," +
                " \"mpa_rating_id\") " +
                "VALUES(?, ?, ?, ?, ?)";
        try {
            MpaRating rating = mpaRatingDbStorage.getMpaRatingById(film.getMpa().getId());
            film.setMpa(rating);

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection ->
                    getPreparedStatement(film, query, connection), keyHolder);

            try {
                int generatedKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
                film.setId(generatedKey);

                Set<Genre> genres = film.getGenres();
                for (var genre : genres) {
                    genreStorage.addGenreToFilm(film.getId(), genre.getId());
                }
            } catch (NullPointerException ex) {
                log.error(ex.getMessage());
            }
            log.debug("Фильм добавлен - " + film);
            return film;
        } catch (DataAccessException ex) {
            log.error("Такого id у рейтинга не существует");
            throw new MpaRatingNotFoundException("Такого id у рейтинга не существует");
        }
    }

    @Override
    @Transactional
    public Film update(Film film) {
        String query = "UPDATE \"film\" SET \"name\" = ?," +
                " \"description\" = ?, " +
                "\"release_date\" = ?, " +
                "\"duration\" = ?," +
                "\"mpa_rating_id\" = ?" +
                "WHERE \"film_id\" = ?";
        try {
            for (var genre : getFilmById(film.getId()).getGenres()) {
                genreStorage.removeGenreFromFilm(film.getId(), genre.getId());
            }

            for (var genre : film.getGenres()) {
                genreStorage.addGenreToFilm(film.getId(), genre.getId());
            }
            List<Genre> sortedList = new ArrayList<>(genreStorage.getFilmGenres(film.getId()));
            sortedList.sort(Comparator.comparing(Genre::getId));
            film.setGenres(new HashSet<>(sortedList));

            MpaRating rating = mpaRatingDbStorage.getMpaRatingById(film.getMpa().getId());
            film.setMpa(rating);


            jdbcTemplate.update(connection -> {
                PreparedStatement ps = getPreparedStatement(film, query, connection);
                ps.setInt(6, film.getId());
                return ps;
            });

            log.debug("Фильм обновлён - " + film);
            return film;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Film delete(int id) {
        String query = "DELETE from \"film\" WHERE \"film_id\" = ?";
        try {
            Film film = getFilmById(id);
            jdbcTemplate.update(query, id);
            log.debug("Фильм удалён - " + film);
            return film;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public int clearAll() {
        String query = "DELETE from \"film\"";
        String restartIdQuery = "ALTER TABLE \"film\" ALTER COLUMN \"film_id\" RESTART WITH 1;";
        try {
            jdbcTemplate.execute(restartIdQuery);
            return jdbcTemplate.update(query);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Collection<Film> getAllObjList() {
        String query = "SELECT * FROM \"film\"";

        try {
            Collection<Film> films = jdbcTemplate.query(query, this::mapRowToFilm);
            log.debug("Количество фильмов - " + films.size());
            return films;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Map<Integer, Film> getFilmsMap() {
        String query = "SELECT * FROM \"film\"";
        Map<Integer, Film> films;
        try {
            Collection<Film> filmsList = jdbcTemplate.query(query, this::mapRowToFilm);
            films = new HashMap<>(filmsList.stream()
                    .collect(Collectors.toMap(Film::getId, item -> item)));
            log.debug("Количество фильмов - " + films.size());
            return films;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Film getFilmById(int id) {
        String query = "SELECT * FROM \"film\" WHERE \"film_id\" = ? LIMIT 1";
        return jdbcTemplate.queryForObject(query, this::mapRowToFilm, id);
    }

    @Override
    public boolean checkForExists(int id) {
        String query = "SELECT count(*) from \"film\" WHERE \"film_id\" = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);
            if (count == null || count == 0) {
                log.error("Фильм с id {} не существует", id);
                return false;
            }
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return false;
        }
        log.debug("Фильм с id={} существует", id);
        return true;
    }

    /**
     * Подготовка запроса для дальнейшего получения идентификатора
     *
     * @param film       Фильм
     * @param query      Запрос
     * @param connection Соединение с бд
     * @return Подготовленный запрос
     * @throws SQLException Когда запрос был неверно составлен
     */
    private PreparedStatement getPreparedStatement(Film film,
                                                   String query,
                                                   Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, film.getName());
        ps.setString(2, film.getDescription());
        ps.setDate(3, Date.valueOf(film.getReleaseDate()));
        ps.setLong(4, film.getDuration());
        ps.setInt(5, film.getMpa().getId());
        return ps;
    }

    /**
     * Маппинг строки в объект фильм
     *
     * @param resultSet Строка
     * @param num       '
     * @return Фильм
     * @throws SQLException Когда мапинг неудался
     */
    private Film mapRowToFilm(ResultSet resultSet, int num) throws SQLException {
        Film film = new Film(
                resultSet.getString("name"),
                LocalDate.parse(resultSet.getString("release_date"),
                        DateTimeFormatter.ofPattern(Constants.dataTimeFormat)));
        film.setId(resultSet.getInt("film_id"));
        film.setDuration(resultSet.getLong("duration"));
        film.setDescription(resultSet.getString("description"));

        MpaRating rating = mpaRatingDbStorage.getMpaRatingById(resultSet.getInt("mpa_rating_id"));
        film.setMpa(rating);

        List<Genre> sortedList = new ArrayList<>(genreStorage.getFilmGenres(film.getId()));
        sortedList.sort(Comparator.comparing(Genre::getId));
        film.setGenres(new HashSet<>(sortedList));
        return film;
    }
}
