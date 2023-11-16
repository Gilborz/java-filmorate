package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController (FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping ("/films")
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping ("/films/{id}")
    public Film getFilmById(@PathVariable Integer id) throws ValidationException {
        validationEmpty(id);
        validationFilm(id);

        return filmService.getFilmById(id);
    }

    @PostMapping ("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        validationDate(film);

        return filmService.addFilm(film);
    }

    @PutMapping ("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        validationFilmId(film);

        return filmService.updateFilm(film);
    }

    @PutMapping ("/films/{id}/like/{userId}")
    public void putLike(
            @PathVariable(required = false) Integer id,
            @PathVariable(required = false) Integer userId) throws ValidationException {
        validationEmpty(id);
        validationEmpty(userId);
        validationFilm(id);
        validationUser(userId);

        filmService.putLikes(id, userId);
    }

    @DeleteMapping ("/films/{id}/like/{userId}")
    public void removeLike(
            @PathVariable(required = false) Integer id,
            @PathVariable(required = false) Integer userId) throws ValidationException {
        validationEmpty(id);
        validationEmpty(userId);
        validationFilm(id);
        validationUser(userId);

        filmService.removeLike(id, userId);
    }

    @GetMapping ("/films/popular")
    public List<Film> getMorePopularFilm(@RequestParam(required = false) Integer count) {
        return filmService.getMorePopularFilm(count);
    }

    private void validationDate(Film film) {
        LocalDate birthDayFilm = LocalDate.of(1895,12,28);

        if (film.getReleaseDate().isBefore(birthDayFilm)) {
            log.error("Дата релиза фильма c указанным id {} раньше 28 декабря 1895 года", film.getId());
            throw new RuntimeException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validationFilmId(Film film) throws ValidationException {
        if (!filmService.getAllFilms().containsKey(film.getId())) {
            log.error("Фильм c id равным " + film.getId() + " не найден");
            throw new ValidationException("Неверный id " + film.getId() + " фильма");
        }
    }

    public void validationEmpty(Integer id) {
        if (id == null) {
            String msg = "Передан пустой id фильма";
            log.error(msg);
            throw new NullPointerException(msg);
        }
    }

    private void validationFilm(Integer filmId) throws ValidationException {
        if (!filmService.getAllFilms().containsKey(filmId)) {
            String msg = "Фильма с таким id " + filmId + " не найдено";
            log.error(msg);
            throw new ValidationException(msg);
        }
    }

    private void validationUser(Integer userId) throws ValidationException {
        if (!filmService.getAllUser().containsKey(userId)) {
            String msg = "Пользователя с id равным " + userId + " не найдено";
            log.error(msg);
            throw new ValidationException(msg);
        }
    }
}
