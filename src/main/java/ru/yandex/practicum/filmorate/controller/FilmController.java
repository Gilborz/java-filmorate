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
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping (value = "/films")
    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping (value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        validation(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Фильм {} добавлен", film);
        return film;
    }

    @PutMapping (value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        for (Integer i : films.keySet()) {
            if (i == film.getId()) {
                films.replace(i, film);
                log.info("Фильм {} обновлён", film);
                return film;
            }
        }

        String msg = "Фильм с таким " + film.getId() + " не найден";
        log.error(msg);
        throw new ValidationException(msg);
    }

    private static void validation(Film film) throws ValidationException {
        LocalDate birthDayFilm = LocalDate.of(1895,12,28);

        if (film.getReleaseDate().isBefore(birthDayFilm)) {
            log.error("Дата релиза {} не может быть раньше 28 декабря 1895 года", film);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
