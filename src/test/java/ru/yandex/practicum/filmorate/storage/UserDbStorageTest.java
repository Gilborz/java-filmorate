package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.SQlDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private UserDbStorage userDbStorage;

    @BeforeEach
    public void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @DirtiesContext
    @Test
    public void testGetAllUsersFromBd() {
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        List<User> addUsers = new ArrayList<>();
        addUsers.add(user);

        userDbStorage.addUser(user);

        List<User> checkUsers = userDbStorage.getAllUsers();

        Assertions.assertThat(checkUsers)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(addUsers);
    }

    @DirtiesContext
    @Test
    public void testAddUserInBd() throws ValidationException {
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        userDbStorage.addUser(user);

        User checkUser = userDbStorage.getUserById(1);

        Assertions.assertThat(checkUser)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(user);
    }

    @DirtiesContext
    @Test
    public void testUpdateUserInBd() throws ValidationException {
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        userDbStorage.addUser(user);

        User newUser = new User(1, "Happy", "Ivan", "@yandex", LocalDate.of(2022, 12,12));
        userDbStorage.updateUser(newUser);

        User checkUser = userDbStorage.getUserById(1);

        Assertions.assertThat(checkUser)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(newUser);

        User handlerUser = new User(3, "Treant", "Fedia", "@gmail", LocalDate.of(2022, 12,12));

        Assertions.assertThatExceptionOfType(SQlDataException.class)
                .isThrownBy(() -> {userDbStorage.updateUser(handlerUser);
                }).withMessage("Пользователь с таким id " + handlerUser.getId() + " не найден");
    }

    @DirtiesContext
    @Test
    public void testGetUserById() throws ValidationException {
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        userDbStorage.addUser(user);

        User checkUser = userDbStorage.getUserById(1);

        Assertions.assertThat(checkUser)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(user);

        Assertions.assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {userDbStorage.getUserById(2);
                }). withMessage("Пользователь не найден");
    }

    @DirtiesContext
    @Test
    public void testAddFriendsById() {
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        User user1 = new User(2, "Treant", "Fedia", "@gmail", LocalDate.of(2022, 12,12));
        userDbStorage.addUser(user);
        userDbStorage.addUser(user1);

        userDbStorage.addFriend(1, 2);

        List<User> checkUser = new ArrayList<>(userDbStorage.getAllFriends(1));

        Assertions.assertThat(checkUser)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator().isEqualTo(new ArrayList<>(List.of(user1)));

        Assertions.assertThatExceptionOfType(SQlDataException.class)
                .isThrownBy(() -> {userDbStorage.addFriend(1, 3);
                }).withMessage("Пользователи не найдены");
    }

    @DirtiesContext
    @Test
    public void testRemoveFriendsById() throws ValidationException {
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        User user1 = new User(2, "Treant", "Fedia", "@gmail", LocalDate.of(2022, 12,12));
        userDbStorage.addUser(user);
        userDbStorage.addUser(user1);
        userDbStorage.addFriend(1, 2);

        userDbStorage.removeFriend(1, 2);
        List<User> checkFriends = userDbStorage.getAllFriends(1);

        Assertions.assertThat(checkFriends)
                .usingRecursiveFieldByFieldElementComparator().isEqualTo(new ArrayList<>());

        Assertions.assertThatExceptionOfType(SQlDataException.class)
                .isThrownBy(() -> {userDbStorage.addFriend(1, 3);
                }).withMessage("Пользователи не найдены");
    }

    @DirtiesContext
    @Test
    void testGetAllFriendsByUser() {
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        User user1 = new User(2, "Treant", "Fedia", "@gmail", LocalDate.of(2022, 12,12));
        userDbStorage.addUser(user);
        userDbStorage.addUser(user1);
        userDbStorage.addFriend(1, 2);

        List<User> checkFriends = userDbStorage.getAllFriends(1);

        Assertions.assertThat(checkFriends)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator().isEqualTo(new ArrayList<>(List.of(user1)));
    }

    @DirtiesContext
    @Test
    public void testGetCommonFriendsByUsers() {
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        User user1 = new User(2, "Treant", "Fedia", "@gmail", LocalDate.of(2022, 12,12));
        User user2 = new User(3, "Treant", "Fedia", "@gmail", LocalDate.of(2022, 12,12));
        userDbStorage.addUser(user);
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);

        userDbStorage.addFriend(1, 3);
        userDbStorage.addFriend(2, 3);

        List<User> checkUsers = userDbStorage.getCommonFriends(1, 2);

        Assertions.assertThat(checkUsers)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator().isEqualTo(new ArrayList<>(List.of(user2)));
    }
}
