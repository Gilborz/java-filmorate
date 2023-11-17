package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws ValidationException {
        validateUser(user);

        return userStorage.updateUser(user);
    }

    public User getUserById(Integer userId) throws ValidationException {
        validateUserId(userId);

        return userStorage.getUserById(userId);
    }

    public void addFriend(Integer userId, Integer friendsId) throws ValidationException {
        validateUserId(userId);
        validateUserId(friendsId);

        User user = userStorage.getUsers().get(userId);
        User friend = userStorage.getUsers().get(friendsId);

        friend.setFriend(userId);
        user.setFriend(friendsId);

    }

    public void removeFriend(Integer userId, Integer friendId) throws ValidationException {
        validateUserId(friendId);

        User user = userStorage.getUsers().get(userId);
        User friend = userStorage.getUsers().get(friendId);

        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public List<User> getFriendsUser(Integer userId) throws ValidationException {
        validateUserId(userId);

        User user = userStorage.getUsers().get(userId);

        List<User> friends = new ArrayList<>();
        for (Integer i : user.getFriends()) {
            friends.add(userStorage.getUsers().get(i));
        }

        log.info("Список друзей пользователя с id {} отправлен", userId);
        return friends;
    }

    public List<User> commonFriends(Integer userId, Integer friendId) throws ValidationException {
        validateUserId(userId);
        validateUserId(friendId);

        Set<Integer> firstUser = userStorage.getUsers().get(userId).getFriends();
        Set<Integer> secondUser = userStorage.getUsers().get(friendId).getFriends();
        Set<Integer> temporarily = new HashSet<>(firstUser);

        temporarily.retainAll(secondUser);
        List<User> commonFriends = new ArrayList<>();
        for (Integer i : temporarily) {
            commonFriends.add(userStorage.getUsers().get(i));
        }

        log.info("Общий список друзей отправлен");
        return commonFriends;
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    private void validateUser(User user) throws ValidationException {
        if (!getUsers().containsKey(user.getId())) {
            log.error("Пользователя с id равным " + user.getId() + " не найдено");
            throw new ValidationException("Неверный id " + user.getId() + " пользователя");
        }
    }

    private void validateUserId(Integer userId) throws ValidationException {
        if (!getUsers().containsKey(userId)) {
            log.error("Пользователя с id равным " + userId + " не найдено");
            throw new ValidationException("Неверный id " + userId + " пользователя");
        }
    }
}
