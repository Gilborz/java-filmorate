package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping ("/films")
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping ("/films/{id}")
    public Film getFilmById(@PathVariable Integer id) throws ValidationException {
        validateEmpty(id);

        return filmService.getFilmById(id);
    }

    @PostMapping ("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        validateDate(film);

        return filmService.addFilm(film);
    }

    @PutMapping ("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.updateFilm(film);
    }

    @PutMapping ("/films/{id}/like/{userId}")
    public void putLike(
            @PathVariable(required = false) Integer id,
            @PathVariable(required = false) Integer userId) throws ValidationException {
        validateEmpty(id);
        validateEmpty(userId);

        filmService.putLikes(id, userId);
    }

    @DeleteMapping ("/films/{id}/like/{userId}")
    public void removeLike(
            @PathVariable(required = false) Integer id,
            @PathVariable(required = false) Integer userId) throws ValidationException {
        validateEmpty(id);
        validateEmpty(userId);

        filmService.removeLike(id, userId);
    }

    @GetMapping ("/films/popular")
    public List<Film> getMorePopularFilm(@RequestParam(required = false) Integer count) {
        return filmService.getMorePopularFilm(count);
    }

    private void validateDate(Film film) {
        LocalDate birthDayFilm = LocalDate.of(1895,12,28);

        if (film.getReleaseDate().isBefore(birthDayFilm)) {
            log.error("Дата релиза фильма c указанным id {} раньше 28 декабря 1895 года", film.getId());
            throw new RuntimeException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    public void validateEmpty(Integer id) {
        if (id == null) {
            String msg = "Передан пустой id фильма";
            log.error(msg);
            throw new NullPointerException(msg);
        }
    }
}
