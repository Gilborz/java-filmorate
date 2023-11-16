package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private Validator validator;
    private FilmController filmController;

    @BeforeEach
    void setup() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void addFilmIfDateHisBeforeBirthdayFilm() {
        LocalDate localDate = LocalDate.of(1800, 10, 23);
        Film film = new Film(1, "film", "desc", localDate, 100);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void updateFilmWithUnknownId() throws ValidationException {
        LocalDate localDate = LocalDate.of(1900, 10, 23);
        Film film = new Film(1, "film", "desc", localDate, 100);
        Film film1 = new Film(99, "film", "desc", localDate, 100);

        filmController.addFilm(film);

        assertThrows(Throwable.class, () -> filmController.updateFilm(film1));
    }

    @Test
    void addFilmIfHisNameEmpty() {
        LocalDate localDate = LocalDate.of(1900, 10, 23);
        Film film = new Film(1, null, "desc", localDate, 100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    void addFilmWithDescriptionMore200() {
        LocalDate localDate = LocalDate.of(1900, 10, 23);
        Film film = new Film(1, "film", "Весной 1942 года по ленинградским улицам" +
                " медленно шли две девочки — Нюра и Рая Ивановы. Впервые после долгой блокадной" +
                " зимы oни отправились пешком с Петроградской стороны на Невский проспект, ко Дворцу пионеров." +
                " Они обходили перевёрнутые трамваи, прятались от взрывов в подворотнях," +
                " пробирались по грудам развалин на тротуарах. 3имой девочки похоронили мать, " +
                " умершую от голода, и остались одни в закопчённой квартире с обледеневшими стенами." +
                " Чтобы согреться, сжигали мебель, одежду, книги.", localDate, 100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    void addFilmWithNegativeDuration() {
        LocalDate localDate = LocalDate.of(1900, 10, 23);
        Film film = new Film(1, "film", "desc", localDate, -100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
    }
}
