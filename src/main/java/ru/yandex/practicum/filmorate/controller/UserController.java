package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private int id = 1;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping (value = "/users")
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping (value = "/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        validation(user);

        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь c id равным {} создан", user.getId());
        return user;
    }

    @PutMapping (value = "/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователя с id равным " + user.getId() + " не найдено");
            throw new ValidationException("Неверный id " + user.getId() + " пользователя");
        }

        users.replace(user.getId(), user);
        log.info("Информация о пользователе c id равным " + user.getId() + " обновлена");
        return user;
    }

    public static void validation(User user) throws ValidationException {
        String[] forEquals = user.getLogin().split(" ");

        if (forEquals.length > 1) {
            log.error("Логин пользователя с id {} содержит пробелы", user.getId());
            throw new ValidationException("Логин не должен содержать пробелы");
        }
    }
}
