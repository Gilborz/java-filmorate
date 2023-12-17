package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Map;

@Component
public interface UserStorage {

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User getUserById(Integer id) throws ValidationException;

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendsId) throws ValidationException;

    List<User> getAllFriends(Integer userId);

    List<User> getCommonFriends(Integer firstId, Integer secondId);

    Map<Integer, User> getUsers();
}
