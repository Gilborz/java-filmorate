package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SQlDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> someUser = getListUsers();

        for (User user : someUser) {
            user.setFriends(getListFriends(user.getId()));
        }

        log.info("Список пользователей отправлен");

        return someUser;
    }

    @Override
    public User addUser(User user) {
        if (user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        saveUser(user);
        saveFriends(user.getFriends(), user.getId());

        log.info("Пользователь с id {} добавлен", user.getId());

        return user;
    }

    @Override
    public User updateUser(User user) {
        String query = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE user_id = ?";

        int rowNum = jdbcTemplate.update(query,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );

        if (rowNum == 0) {
            log.info("Пользователь не найден, id равен {}", user.getId());
            throw new SQlDataException("Пользователь с таким id " + user.getId() + " не найден");
        }

        deleteFriends(user.getId());
        saveFriends(user.getFriends(), user.getId());

        log.info("Информация о пользователе с id {} обновлена", user.getId());
        return user;
    }

    @Override
    public User getUserById(Integer id) throws ValidationException {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try {
            User user = jdbcTemplate.queryForObject(query, userRowMapper(), id);
            user.setFriends(getListFriends(user.getId()));

            log.info("Пользователь с id {} отправлен", id);
            return user;
        } catch (DataAccessException e) {
            log.info("Пользователь с таким id {} не найден", id);
            throw new ValidationException("Пользователь не найден");
        }
    }

    public void addFriend(Integer userId, Integer friendId) {
        try {
            String query = "INSERT INTO friendship (user_id, friends_id) VALUES (?, ?)";

            jdbcTemplate.update(query, userId, friendId);
            log.info("Пользователь с id {} добавлен в друзья к пользователю с id {}", friendId, userId);

        } catch (DataAccessException e) {
            log.info("Пользователи не найдены");
            throw new SQlDataException("Пользователи не найдены");
        }
    }

    public void removeFriend(Integer userId, Integer friendsId) throws ValidationException {
        String query = "DELETE FROM friendship WHERE user_id = ? AND friends_id = ?";

        int rowNum = jdbcTemplate.update(query, userId, friendsId);
        log.info("Пользователь с id {} из друзей пользователя с id {} удалён", userId, friendsId);

        if (rowNum == 0) {
            log.info("Пользователи не найдены");
            throw new ValidationException("Пользователей с такими id не найдено");
        }
    }

    public List<User> getAllFriends(Integer userId) {
        String query = "SELECT user_id, login, name, email, birthday FROM users u " +
                "JOIN (SELECT friends_id FROM friendship WHERE user_id = ?) f ON u.user_id = f.friends_id";

        log.info("Список друзей пользователя с id {} отправлен", userId);
        return new ArrayList<>(jdbcTemplate.query(query, userRowMapper(), userId));
    }

    public List<User> getCommonFriends(Integer firstId, Integer secondId) {
        String query = "SELECT * FROM users WHERE user_id IN " +
                "(SELECT friends_id FROM friendship f WHERE f.user_id = ? INTERSECT " +
                "SELECT friends_id FROM friendship f WHERE f.user_id = ?)";

        List<User> someUser = new ArrayList<>(jdbcTemplate.query(query, userRowMapper(), firstId, secondId));

        for (User user : someUser) {
            user.setFriends(getListFriends(user.getId()));
        }

        log.info("Список общих друзей отправлен");
        return someUser;
    }

    private List<User> getListUsers() {
        String query = "SELECT * FROM users";

        return new ArrayList<>(jdbcTemplate.query(query, userRowMapper()));
    }

    private List<Integer> getListFriends(Integer userId) {
        String query = "SELECT friends_id FROM friendship WHERE user_id = ?";

        return new ArrayList<>(jdbcTemplate.query(query, integerRowMapper(), userId));
    }

    private void saveUser(User user) {
        String query = "INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(query, new String[]{"user_id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
    }

    private void saveFriends(List<Integer> friends, Integer idUser) {
        if (friends.size() > 0) {
            for (Integer friend : friends) {
                String query = "INSERT INTO friendship (user_id, friends_id) VALUES (?, ?)";
                jdbcTemplate.update(query, idUser, friend);
            }
        }
    }

    private void deleteFriends(Integer idUser) {
        String query = "DELETE FROM friendship WHERE user_id = ?";

        jdbcTemplate.update(query, idUser);
    }

    private RowMapper<User> userRowMapper() {
        return ((rs, rowNum) -> new User(
                rs.getInt("user_id"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getDate("birthday").toLocalDate())
        );
    }

    private RowMapper<Integer> integerRowMapper() {
        return ((rs, rowNum) -> new Integer(rs.getInt("friends_id")));
    }

    @Override
    public Map<Integer, User> getUsers() {
        return null;
    }
}
