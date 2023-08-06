package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserMapper {
    private UserMapper() {
    }

    public static User mapRowToUser(ResultSet resultSet, int num) throws SQLException {
        User user = new User(
                resultSet.getString("email"),
                resultSet.getString("login"),
                LocalDate.parse(resultSet.getString("birthday"),
                        DateTimeFormatter.ofPattern(Constants.dataTimeFormat)));
        user.setId(resultSet.getInt("user_id"));
        user.setName(resultSet.getString("name"));
        return user;
    }
}
