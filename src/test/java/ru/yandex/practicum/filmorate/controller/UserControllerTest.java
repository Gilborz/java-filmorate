package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private Validator validator;
    private UserController userController;

    @BeforeEach
    void setup() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createUserIfLoginHaveVoidOrEmpty() throws IOException, InterruptedException {
        LocalDate localDate = LocalDate.of(2000,12,12);
        User user = new User(1, "jkl lkj", "Bill", "mail@ru", localDate);
        User user1 = new User(1, null, "Bill", "mail@ru", localDate);

        Set<ConstraintViolation<User>> violations = validator.validate(user1);

        assertFalse(violations.isEmpty());
        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void createUserIfNameEqualsNullThenNameEqualsLogin() throws ValidationException {
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

        assertThrows(ValidationException.class, () -> userController.updateUser(user1));
    }

    @Test
    void createUserWithIncorrectEmail() {
        LocalDate localDate = LocalDate.of(2000,12,12);
        User user = new User(1, "jkl lkj", "Bill", "mailru", localDate);
        User user1 = new User(1, "jkl lkj", "Bill", null, localDate);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Set<ConstraintViolation<User>> violations1 = validator.validate(user1);

        assertFalse(violations.isEmpty());
        assertFalse(violations1.isEmpty());
    }

    @Test
    void createUserIfBirthdayInFuture() {
        LocalDate localDate = LocalDate.of(3000,12,12);
        User user = new User(1, "jkl lkj", "Bill", "mail@ru", localDate);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
    }
}