package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Хранилище для пользователей в бд
 */
@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        String query = "INSERT INTO \"user\" (\"email\", \"login\", \"name\", \"birthday\") " +
                "VALUES(?, ?, ?, ?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection ->
                    getPreparedStatement(user, query, connection), keyHolder);

            try {
                int generatedKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
                user.setId(generatedKey);
            } catch (NullPointerException ex) {
                log.error(ex.getMessage());
            }
            log.debug("Пользователь добавлен - " + user);
            return user;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Transactional
    @Override
    public User update(User user) {
        String query = "UPDATE \"user\" SET \"email\" = ?," +
                " \"login\" = ?, " +
                "\"name\" = ?, " +
                "\"birthday\" = ?" +
                "WHERE \"user_id\" = ?";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = getPreparedStatement(user, query, connection);
                ps.setInt(5, user.getId());
                return ps;
            }, keyHolder);

            try {
                int generatedKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
                user.setId(generatedKey);
            } catch (NullPointerException ex) {
                log.error(ex.getMessage());
            }
            log.debug("Пользователь добавлен - " + user);
            return user;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public User delete(int id) {
        String query = "DELETE from \"user\" WHERE \"user_id\" = ?";
        try {
            User user = getUserById(id);
            jdbcTemplate.update(query, id);
            log.debug("Пользователь удалён - " + user);
            return user;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public int clearAll() {
        String query = "DELETE from \"user\"";
        String restartIdQuery = "ALTER TABLE \"user\" ALTER COLUMN \"user_id\" RESTART WITH 1;";
        try {
            jdbcTemplate.execute(restartIdQuery);
            return jdbcTemplate.update(query);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Collection<User> getAllObjList() {
        String query = "SELECT * FROM \"user\"";

        try {
            Collection<User> users = jdbcTemplate.query(query, UserMapper::mapRowToUser);
            log.debug("Количество пользователей - " + users.size());
            return users;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Map<Integer, User> getUsersMap() {
        String query = "SELECT * FROM \"user\"";
        Map<Integer, User> users;
        try {
            Collection<User> usersList = jdbcTemplate.query(query, UserMapper::mapRowToUser);
            users = new HashMap<>(usersList.stream()
                    .collect(Collectors.toMap(User::getId, item -> item)));
            log.debug("Количество пользователей - " + users.size());
            return users;
        } catch (DataAccessException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public User getUserById(int id) {
        String query = "SELECT * FROM \"user\" WHERE \"user_id\" = ? LIMIT 1";
        return jdbcTemplate.queryForObject(query, UserMapper::mapRowToUser, id);
    }

    @Override
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM \"user\" WHERE \"email\" = ? LIMIT 1";
        List<User> users = jdbcTemplate.query(query, UserMapper::mapRowToUser, email);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User getUserByLogin(String login) {
        String query = "SELECT * FROM \"user\" WHERE \"login\" = ? LIMIT 1";
        List<User> users = jdbcTemplate.query(query, UserMapper::mapRowToUser, login);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public boolean checkForExists(int id) {
        String query = "SELECT count(*) from \"user\" WHERE \"user_id\" = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);
            if (count == null || count == 0) {
                log.error("Пользователь с id {} не существует", id);
                return false;
            }
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return false;
        }
        log.debug("Пользователь с id={} существует", id);
        return true;
    }

    /**
     * Подготовка запроса для дальнейшего получения идентификатора
     *
     * @param user       Пользователь
     * @param query      Запрос
     * @param connection Соединение с бд
     * @return Подготовленный запрос
     * @throws SQLException Когда запрос был неверно составлен
     */
    private PreparedStatement getPreparedStatement(User user,
                                                   String query,
                                                   Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query,
                Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getLogin());
        ps.setString(3, user.getName());
        ps.setDate(4, Date.valueOf(user.getBirthday()));
        return ps;
    }
}
