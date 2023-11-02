package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setFilmController() {
        filmController = new FilmController();
    }
    @Test
    void testOfDateFilmBeforeBirthdayFilm() {
        LocalDate localDate = LocalDate.of(1800,10,23);
        Film film = new Film(1,"film","desc", localDate, 100);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void updateFilmWithUnknownId() throws ValidationException {
        LocalDate localDate = LocalDate.of(1900,10,23);
        Film film = new Film(1,"film","desc", localDate, 100);
        Film film1 = new Film(99,"film","desc", localDate, 100);

        filmController.addFilm(film);

        assertThrows(Throwable.class, () -> filmController.updateFilm(film1));
    }
}