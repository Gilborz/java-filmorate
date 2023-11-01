package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private int id = 0;
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> allFilms() {
        List<Film> filmList = new ArrayList<>(films.values());
        return filmList;
    }

    @PostMapping (value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        try {
            LocalDateTime birthDayFilm = LocalDateTime.of(1895,12,28,0,0);

            if (film.getReleaseDate().isBefore(birthDayFilm)) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }

            log.info("Фильм добавлен {}", film);
            film.setId(id++);
            films.put(film.getId(), film);
            return film;
        } catch (ValidationException e) {
            log.error(e.getMessage());
            return film;
        }
    }

    @PutMapping (value = "/films")
    public Film updateFilm(@RequestBody Film film) {
        try {
            for (Integer i : films.keySet()) {
                if (i == film.getId()) {
                    log.info("Фильм обновлён {}", film);
                    films.put(film.getId(), film);
                    return film;
                }
            }

            throw new ValidationException("Фильма с таким " + film.getId() + " не существует");
        } catch (ValidationException e) {
            log.debug(e.getMessage());
            return film;
        }
    }
}
