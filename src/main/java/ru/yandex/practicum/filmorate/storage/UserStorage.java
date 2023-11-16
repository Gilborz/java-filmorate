package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Map;

@Component
public interface UserStorage {

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User getUserById(Integer id);

    Map<Integer, User> getUsers();
}
