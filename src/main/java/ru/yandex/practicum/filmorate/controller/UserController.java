package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;

@Slf4j
@RestController
public class UserController {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserController(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    @GetMapping ("/users")
    public List<User> getUsers() {
        return userDbStorage.getAllUsers();
    }

    @GetMapping ("/users/{id}")
    public User getUserId(@PathVariable(required = false) Integer id) throws ValidationException {
        validateEmpty(id);

        return userDbStorage.getUserById(id);
    }

    @PostMapping ("/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        validateDate(user);

        return userDbStorage.addUser(user);
    }

    @PutMapping ("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        return userDbStorage.updateUser(user);
    }

    @PutMapping ("/users/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable(required = false) Integer id,
            @PathVariable(required = false) Integer friendId) throws ValidationException {
        validateEmpty(id);
        validateEmpty(friendId);

        userDbStorage.addFriend(id, friendId);
    }

    @DeleteMapping ("/users/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable(required = false) Integer id,
            @PathVariable(required = false) Integer friendId) throws ValidationException {
        validateEmpty(id);
        validateEmpty(friendId);

        userDbStorage.removeFriend(id, friendId);
    }

    @GetMapping ("/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable(required = false) Integer id) throws ValidationException {
        validateEmpty(id);

        return userDbStorage.getAllFriends(id);
    }

    @GetMapping ("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable(required = false) Integer id,
            @PathVariable(required = false) Integer otherId) throws ValidationException {
        validateEmpty(id);
        validateEmpty(otherId);

        return userDbStorage.getCommonFriends(id, otherId);
    }

    private void validateEmpty(Integer id) {
        if (id == null) {
            String msg = "Передан пустой id пользователя";
            log.error(msg);
            throw new NullPointerException(msg);
        }
    }

    private void validateDate(User user) throws ValidationException {
        String[] forEquals = user.getLogin().split(" ");

        if (forEquals.length > 1) {
            log.error("Логин пользователя с id {} содержит пробелы", user.getId());
            throw new ValidationException("Логин не должен содержать пробелы");
        }
    }
}
