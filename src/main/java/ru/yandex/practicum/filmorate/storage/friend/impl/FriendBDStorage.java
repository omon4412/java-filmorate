package ru.yandex.practicum.filmorate.storage.friend.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class FriendBDStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendBDStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String query = "INSERT INTO \"user_friend\" (\"user_id_1\", \"user_id_2\") " +
                "VALUES(?, ?)";
        try {
            jdbcTemplate.update(query, userId, friendId);
            log.debug("Пользователь id=" + userId + " добавил в друзья id=" + friendId);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String query = "DELETE FROM \"user_friend\" " +
                "WHERE (\"user_id_1\"=? and \"user_id_2\"=?)" +
                "or (\"user_id_2\"=? and \"user_id_1\"=?)";
        try {
            jdbcTemplate.update(query, userId, friendId, userId, friendId);
            log.debug("Пользователь id=" + userId + " удалил из друзей id=" + friendId);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Set<Integer> getUserFriendsIds(int userId) {
        String query = "SELECT u1.\"user_id_1\"\n" +
                "FROM \"user_friend\" AS u1\n" +
                "WHERE u1.\"user_id_2\" = ? and \"status\"=true\n" +
                "UNION\n" +
                "SELECT u2.\"user_id_2\"\n" +
                "FROM \"user_friend\" AS u2\n" +
                "WHERE u2.\"user_id_1\" = ?;";

        try {
            List<Integer> users = jdbcTemplate.query(query, (rs, rowNum) ->
                    rs.getInt("user_id_1"), userId, userId);
            log.debug("Количество друзей - " + users.size());
            return new HashSet<>(users);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void removeAll() {
        String truncateQuery = "DELETE from \"user_friend\"";
        try {
            jdbcTemplate.update(truncateQuery);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }
}
