package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    private int id = 1;
    private static Map<Integer, Film> films = new HashMap<>();

    @GetMapping (value = "/films")
    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping (value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        validation(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Фильм c id равным " + film.getId() + " добавлен");
        return film;
    }

    @PutMapping (value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм c id равным " + film.getId() + " не найден");
            throw new ValidationException("Неверный id " + film.getId() + " фильма");
        }

        films.replace(film.getId(), film);
        log.info("Фильм c id равным " + film.getId() + " обновлён");
        return film;
    }

    private static void validation(Film film) throws ValidationException {
        LocalDate birthDayFilm = LocalDate.of(1895,12,28);

        if (film.getReleaseDate().isBefore(birthDayFilm)) {
            log.error("Дата релиза фильма c указанным id {} раньше 28 декабря 1895 года", film.getId());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
