package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService (UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById (Integer id) {
        return userStorage.getUserById(id);
    }

    public void addFriends(Integer userId, Integer friendsId) {
        User user = userStorage.getUsers().get(userId);
        User friend = userStorage.getUsers().get(friendsId);

        friend.setFriend(userId);
        user.setFriend(friendsId);

    }

    public void removeFriend(Integer userId, Integer friendsId) {
        User user = userStorage.getUsers().get(userId);
        User friend = userStorage.getUsers().get(friendsId);

        user.removeFriend(friendsId);
        friend.removeFriend(userId);
    }

    public List<User> getFriendsUser(Integer userId) {
        User user = userStorage.getUsers().get(userId);

        List<User> friends = new ArrayList<>();
        for(Integer i : user.getFriends()) {
            friends.add(userStorage.getUsers().get(i));
        }

        log.info("Список друзей пользователя с id {} отправлен", userId);
        return friends;
    }

    public List<User> commonFriends(Integer userId, Integer friendsId) {
        Set<Integer> firstUser = userStorage.getUsers().get(userId).getFriends();
        Set<Integer> secondUser = userStorage.getUsers().get(friendsId).getFriends();

        firstUser.retainAll(secondUser);

        List<User> commonFriends = new ArrayList<>();
        for(Integer i : firstUser) {
            commonFriends.add(userStorage.getUsers().get(i));
        }

        log.info("Общий список друзей отправлен");
        return commonFriends;
    }
}
