package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public FilmService (FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public void putLikes(Integer filmId, Integer userId) {
        getAllFilms().get(filmId).setLike(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
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
}

class FilmComparator implements Comparator<Film> {

    @Override
    public int compare(Film film1, Film film2) {
        return film1.getLikes().size() - film2.getLikes().size();
    }
}