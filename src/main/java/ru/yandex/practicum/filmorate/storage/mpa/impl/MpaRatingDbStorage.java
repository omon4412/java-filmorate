package ru.yandex.practicum.filmorate.storage.mpa.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

/**
 * Хранилище для рейтингов в бд.
 */
@Component
@Slf4j
public class MpaRatingDbStorage implements MpaRatingStorage {
    /**
     * JdbcTemplate для взаимодействия с базой данных.
     */
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MpaRating add(MpaRating mpaRating) {
        final String query = "INSERT INTO \"mpa_rating\" (\"name\") " +
                "VALUES(?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, mpaRating.getName());
                return ps;
            }, keyHolder);

            try {
                int generatedKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
                mpaRating.setId(generatedKey);
            } catch (NullPointerException ex) {
                log.error(ex.getMessage());
            }
            log.debug("Рейтинг добавлен - " + mpaRating);
            return mpaRating;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public MpaRating update(MpaRating mpaRating) {
        String query = "UPDATE \"mpa_rating\" SET \"name\" = ?" +
                "WHERE \"mpa_rating_id\" = ?";
        try {
            jdbcTemplate.update(query, mpaRating.getId());
            log.debug("Рейтинг обновлён - " + mpaRating);
            return mpaRating;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public MpaRating delete(int id) {
        String query = "DELETE from \"mpa_rating\" WHERE \"mpa_rating_id\" = ?";
        try {
            MpaRating mpaRating = getMpaRatingById(id);
            jdbcTemplate.update(query, id);
            log.debug("Рейтинг удалён - " + mpaRating);
            return mpaRating;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public int clearAll() {
        String query = "DELETE from \"mpa_rating\"";
        String restartIdQuery = "ALTER TABLE \"mpa_rating\" ALTER COLUMN \"mpa_rating_id\" RESTART WITH 1;";
        try {
            jdbcTemplate.execute(restartIdQuery);
            int sqlResult = jdbcTemplate.update(query);
            log.debug("Все рейтингы удалены");
            return sqlResult;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Collection<MpaRating> getAllObjList() {
        String query = "SELECT * FROM \"mpa_rating\"";

        try {
            Collection<MpaRating> mpaRatings = jdbcTemplate.query(query,
                    (rs, num) -> new MpaRating(rs.getInt("mpa_rating_id"),
                            rs.getString("name")));
            log.debug("Количество рейтингов - " + mpaRatings.size());
            return mpaRatings;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public MpaRating getMpaRatingById(int id) {
        String query = "SELECT * FROM \"mpa_rating\" WHERE \"mpa_rating_id\" = ? LIMIT 1";
        return jdbcTemplate.queryForObject(query, (rs, num) -> {
            MpaRating mpaRating = new MpaRating(rs.getInt("mpa_rating_id"),
                    rs.getString("name"));

            log.debug("Получен рейтинг - " + mpaRating);
            return mpaRating;
        }, id);
    }

    @Override
    public boolean checkForExists(int id) {
        String query = "SELECT count(*) from \"mpa_rating\" WHERE \"mpa_rating_id\" = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);
            if (count == null || count == 0) {
                log.error("Рейтинг с id {} не существует", id);
                return false;
            }
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return false;
        }
        log.debug("Рейтинг с id={} существует", id);
        return true;
    }
}
