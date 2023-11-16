package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int id;
    private Map<Integer, Film> films;

    public InMemoryFilmStorage () {
        this.films = new HashMap<>();
        this.id = 1;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        log.info("Информация о фильме c id равным " + id + " отправлена");
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Фильм c id равным " + film.getId() + " добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.replace(film.getId(), film);
        log.info("Фильм c id равным " + film.getId() + " обновлён");
        return film;
    }

    public Map<Integer, Film> getFilms() {
        return films;
    }
}
