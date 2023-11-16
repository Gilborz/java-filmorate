package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private int id;
    private Map<Integer, User> users;

    public InMemoryUserStorage() {
        this.id = 1;
        this.users = new HashMap<>();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        if (user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь c id равным {} создан", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.replace(user.getId(), user);
        log.info("Информация о пользователе c id равным " + user.getId() + " обновлена");
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        log.info("Информация о пользователе c id равным " + id + " отправлена");
        return users.get(id);
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }
}
