package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> allUsers() {
        List<User> userList = new ArrayList<>(users.values());
        return userList;
    }

    @PostMapping (value = "/users")
    public User addFilm(@RequestBody User user) {
        return user;
    }

    @PutMapping (value = "/users")
    public User updateUser(@RequestBody User user) {
        for (Integer i : users.keySet()) {
            if (i == user.getId()) {
                users.put(user.getId(), user);
                return user;
            }
        }

        return user;
    }
}
