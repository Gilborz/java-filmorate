package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException {
        validateFilm(film);

        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer filmId) throws ValidationException {
        validateFilmId(filmId);

        return filmStorage.getFilmById(filmId);
    }

    public void putLikes(Integer filmId, Integer userId) throws ValidationException {
        validateFilmId(filmId);
        validateUserId(userId);

        getAllFilms().get(filmId).setLike(userId);
    }

    public void removeLike(Integer filmId, Integer userId) throws ValidationException {
        validateFilmId(filmId);
        validateUserId(userId);

        getAllFilms().get(filmId).removeLike(userId);
    }

    public List<Film> getMorePopularFilm(Integer count) {
        if (count == null) {
            count = 10;
        }

        int num = count;

        List<Film> films = getFilms();
        films.sort(new FilmComparator().reversed());

        if (num > films.size()) {
            log.info("Список из {} фильмов отправлен", films.size());
            return films;
        }

        log.info("Список из {} фильмов отправлен", count);
        return films.subList(0, num);
    }

    public Map<Integer, Film> getAllFilms() {
        return filmStorage.getFilms();
    }

    public Map<Integer, User> getAllUser() {
        return userStorage.getUsers();
    }

    private void validateFilm(Film film) throws ValidationException {
        if (!getAllFilms().containsKey(film.getId())) {
            log.error("Фильм c id равным " + film.getId() + " не найден");
            throw new ValidationException("Неверный id " + film.getId() + " фильма");
        }
    }

    private void validateFilmId(Integer filmId) throws ValidationException {
        if (!getAllFilms().containsKey(filmId)) {
            String msg = "Фильма с таким id " + filmId + " не найдено";
            log.error(msg);
            throw new ValidationException(msg);
        }
    }

    private void validateUserId(Integer userId) throws ValidationException {
        if (!getAllUser().containsKey(userId)) {
            String msg = "Пользователя с id равным " + userId + " не найдено";
            log.error(msg);
            throw new ValidationException(msg);
        }
    }
}