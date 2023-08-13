package ru.yandex.practicum.filmorate.storage.genre.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre add(Genre genre) {
        final String query = "INSERT INTO \"genre\" (\"name\") " +
                "VALUES(?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, genre.getName());
                return ps;
            }, keyHolder);

            try {
                int generatedKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
                genre.setId(generatedKey);
            } catch (NullPointerException ex) {
                log.error(ex.getMessage());
            }
            log.debug("Жанр добавлен - " + genre);
            return genre;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Genre update(Genre genre) {
        String query = "UPDATE \"genre\" SET \"name\" = ?" +
                "WHERE \"genre_id\" = ?";
        try {
            jdbcTemplate.update(query, genre.getId());
            log.debug("Жанр обновлён - " + genre);
            return genre;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Genre delete(int id) {
        String query = "DELETE from \"genre\" WHERE \"genre_id\" = ?";
        try {
            Genre genre = getGenreById(id);
            jdbcTemplate.update(query, id);
            log.debug("Жанр удалён - " + genre);
            return genre;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public int clearAll() {
        String query = "DELETE from \"genre\"";
        String restartIdQuery = "ALTER TABLE \"genre\" ALTER COLUMN \"genre_id\" RESTART WITH 1;";
        try {
            jdbcTemplate.execute(restartIdQuery);
            int sqlResult = jdbcTemplate.update(query);
            log.debug("Все жанры удалены");
            return sqlResult;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Collection<Genre> getAllObjList() {
        String query = "SELECT * FROM \"genre\"";

        try {
            Collection<Genre> genres = jdbcTemplate.query(query,
                    (rs, num) -> new Genre(rs.getInt("genre_id"),
                            rs.getString("name")));
            log.debug("Количество жанров - " + genres.size());
            return genres;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Genre getGenreById(int id) {
        String query = "SELECT * FROM \"genre\" WHERE \"genre_id\" = ? LIMIT 1";
        return jdbcTemplate.queryForObject(query, (rs, num) -> {
            Genre genre = new Genre(rs.getString("name"));
            genre.setId(id);
            log.debug("Получен жанр - " + genre);
            return genre;
        }, id);
    }

    @Override
    public boolean checkForExists(int id) {
        String query = "SELECT count(*) from \"genre\" WHERE \"genre_id\" = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);
            if (count == null || count == 0) {
                log.error("Жанр с id {} не существует", id);
                return false;
            }
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return false;
        }
        log.debug("Жанр с id={} существует", id);
        return true;
    }
}
