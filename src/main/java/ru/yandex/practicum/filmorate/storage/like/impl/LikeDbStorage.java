package ru.yandex.practicum.filmorate.storage.like.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Реализация хранения друзей в БД
 */
@Component
@Slf4j
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeToFilm(int filmId, int userId) {
        String query = "INSERT INTO \"film_user_like\" (\"user_id\", \"film_id\") " +
                "VALUES(?, ?)";
        try {
            jdbcTemplate.update(query, userId, filmId);
            log.debug("Пользователь id=" + userId + " лайкнул фильм id=" + filmId);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void removeLikeFromFilm(int filmId, int userId) {
        String query = "DELETE FROM \"film_user_like\" " +
                "WHERE (\"film_id\"=? and \"user_id\"=?);";
        try {
            jdbcTemplate.update(query, filmId, userId);
            log.debug("Пользователь id=" + userId + " удалил лайк у фильма id=" + filmId);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Set<Integer> getUsersLikesIds(int filmId) {
        String query = "SELECT \"user_id\"\n" +
                "FROM \"film_user_like\"\n" +
                "WHERE \"film_id\" = ?";

        try {
            List<Integer> users = jdbcTemplate.query(query, (rs, rowNum) ->
                    rs.getInt("user_id"), filmId);
            log.debug("Количество лайков - " + users.size());
            return new HashSet<>(users);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public int getLikesCount(int filmId) {
        String query = "SELECT count(*) as c\n" +
                "FROM \"film_user_like\"\n" +
                "WHERE \"film_id\" = ?";
        try {
            Integer likesCount = jdbcTemplate.queryForObject(query, Integer.class, filmId);
            if (likesCount == null) {
                log.debug("Из БД вернулось null");
                return 0;
            }
            log.debug("Количество лайков - " + likesCount);
            return likesCount;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void removeAll() {
        String truncateQuery = "DELETE from \"film_user_like\"";
        try {
            jdbcTemplate.update(truncateQuery);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }
}
