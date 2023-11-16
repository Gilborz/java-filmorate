package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.List;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping ("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping ("/users/{id}")
    public User getUserId(@PathVariable (required = false) Integer id) throws ValidationException {
        validationEmpty(id);
        validationUserId(id);

        return userService.getUserById(id);
    }

    @PostMapping ("/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        validationDate(user);

        return userService.addUser(user);
    }

    @PutMapping ("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        validationUser(user);

        return userService.updateUser(user);
    }

    @PutMapping ("/users/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable(required = false) Integer id,
            @PathVariable(required = false) Integer friendId) throws ValidationException {
        validationEmpty(id);
        validationUserId(id);
        validationEmpty(friendId);
        validationUserId(friendId);

        userService.addFriends(id, friendId);
    }

    @DeleteMapping ("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable(required = false) Integer id, @PathVariable(required = false) Integer friendId) throws ValidationException {
        validationEmpty(id);
        validationEmpty(friendId);
        validationUserId(friendId);

        userService.removeFriend(id, friendId);
    }

    @GetMapping ("/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable(required = false) Integer id) throws ValidationException {
        validationEmpty(id);
        validationUserId(id);

        return userService.getFriendsUser(id);
    }

    @GetMapping ("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(required = false) Integer id, @PathVariable(required = false) Integer otherId) throws ValidationException {
        validationEmpty(id);
        validationEmpty(otherId);
        validationUserId(id);
        validationUserId(otherId);

        return userService.commonFriends(id, otherId);
    }

    private void validationEmpty(Integer id) {
        if (id == null) {
            String msg = "Передан пустой id пользователя";
            log.error(msg);
            throw new NullPointerException(msg);
        }
    }

    private void validationDate(User user) throws ValidationException {
        String[] forEquals = user.getLogin().split(" ");

        if (forEquals.length > 1) {
            log.error("Логин пользователя с id {} содержит пробелы", user.getId());
            throw new ValidationException("Логин не должен содержать пробелы");
        }
    }

    private void validationUser(User user) throws ValidationException {
        if (!userService.getUsers().containsKey(user.getId())) {
            log.error("Пользователя с id равным " + user.getId() + " не найдено");
            throw new ValidationException("Неверный id " + user.getId() + " пользователя");
        }
    }

    private void validationUserId(Integer userId) throws ValidationException {
        if (!userService.getUsers().containsKey(userId)) {
            log.error("Пользователя с id равным " + userId + " не найдено");
            throw new ValidationException("Неверный id " + userId + " пользователя");
        }
    }
}
