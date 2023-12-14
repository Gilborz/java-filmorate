package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Data
@EqualsAndHashCode (exclude = {"id"})
public class User {
    private Integer id;
    @NotBlank(message = "Поле не должно быть пустым")
    private String login;
    private String name;
    @Email(message = "Некорректный email")
    @NotNull(message = "Поле не должно быть пустым")
    private String email;
    @Past(message = "День рождения не корректный")
    private LocalDate birthday;
    private List<Integer> friends;

    public User(Integer id, String login, String name, String email, LocalDate birthday) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.friends = new ArrayList<>();
    }

    public void setFriend(Integer friend) {
        friends.add(friend);
        log.info("Пользователь с id {} добавлен в друзья", friend);
    }

    public void removeFriend(Integer friend) {
        friends.remove(friend);
        log.info("Пользователь с id {} удалён из друзей", friend);
    }
}
