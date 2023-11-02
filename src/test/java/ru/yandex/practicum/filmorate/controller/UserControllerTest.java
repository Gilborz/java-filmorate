package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUserController() {
        userController = new UserController();
    }

    @Test
    void ifLoginHaveVoid() {
        LocalDate localDate = LocalDate.of(2000,12,12);
        User user = new User(1, "jkl lkj", "Bill", "mail@ru", localDate);

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void ifNameEqualsNullThenNameEqualsLogin() throws ValidationException {
        LocalDate localDate = LocalDate.of(2000,12,12);
        User user = new User(1, "jkllkj", null, "mail@ru", localDate);

        final User load = userController.addUser(user);
        final User save = userController.getUsers().get(0);

        assertEquals(save, load);
    }

    @Test
    void updateUserWithUnknownId() throws ValidationException {
        LocalDate localDate = LocalDate.of(2000,12,12);
        User user = new User(1, "jkllkj", "Bill", "mail@ru", localDate);
        User user1 = new User(99, "jkllkj", "Bill", "mail@ru", localDate);

        userController.addUser(user);

        assertThrows(Throwable.class, () -> userController.updateUser(user1));
    }
}