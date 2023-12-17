package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.SQlDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Motion;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmDbStorage;

    @BeforeEach
    public void setUp() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    @DirtiesContext
    @Test
    public void testGetAllFilms() {
        Film film = new Film(1, "Babai", "Skazka", 120, LocalDate.of(2000, 10, 10), new Motion(1, "G"));
        filmDbStorage.addFilm(film);

        List<Film> checkFilm = filmDbStorage.getAllFilms();

        Assertions.assertThat(checkFilm)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator().isEqualTo(new ArrayList<>(List.of(film)));
    }

    @DirtiesContext
    @Test
    public void testAddFilmInBd() throws ValidationException {
        Film film = new Film(1, "Babai", "Skazka", 120, LocalDate.of(2000, 10, 10), new Motion(1, "G"));

        filmDbStorage.addFilm(film);
        Film checkFilm = filmDbStorage.getFilmById(1);

        Assertions.assertThat(checkFilm)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(film);
    }

    @DirtiesContext
    @Test
    public void testUpdateFilm() throws ValidationException {
        Film film = new Film(1, "Babai", "Skazka", 120, LocalDate.of(2000, 10, 10), new Motion(1, "G"));
        filmDbStorage.addFilm(film);

        Film newFilm = new Film(1, "Ali", "Skazka", 200, LocalDate.of(2001, 10, 10), new Motion(1, "G"));
        filmDbStorage.updateFilm(newFilm);
        Film checkFilm = filmDbStorage.getFilmById(1);

        Assertions.assertThat(checkFilm)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(newFilm);

        Film handlerFilm = new Film(3, "Forest", "Skazka", 160, LocalDate.of(1998, 10, 10), new Motion(1, "PG"));

        Assertions.assertThatExceptionOfType(SQlDataException.class)
                .isThrownBy(() -> {
                    filmDbStorage.updateFilm(handlerFilm);
                }).withMessage("Фильм с таким id " + handlerFilm.getId() + " не найден");
    }

    @DirtiesContext
    @Test
    public void testGetFilmById() throws ValidationException {
        Film film = new Film(1, "Babai", "Skazka", 120, LocalDate.of(2000, 10, 10), new Motion(1, "G"));
        filmDbStorage.addFilm(film);

        Film checkFilm = filmDbStorage.getFilmById(1);

        Assertions.assertThat(checkFilm)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(film);

        Assertions.assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    filmDbStorage.getFilmById(2);
                }).withMessage("Фильм не найден, id равен " + 2);
    }

    @DirtiesContext
    @Test
    public void testAddLikeToFilm() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        Film film = new Film(1, "Babai", "Skazka", 120, LocalDate.of(2000, 10, 10), new Motion(1, "G"));
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        userDbStorage.addUser(user);
        filmDbStorage.addFilm(film);

        filmDbStorage.addLike(1, 1);
        List<Film> checkList = filmDbStorage.getPopular(null);

        Assertions.assertThat(checkList)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator().isEqualTo(new ArrayList<>(List.of(film)));

        Assertions.assertThatExceptionOfType(SQlDataException.class)
                .isThrownBy(() -> {
                    filmDbStorage.addLike(1, 3);
                }).withMessage("Фильм или пользователь не найдены");
    }

    @DirtiesContext
    @Test
    public void testDeleteLike() throws ValidationException {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        Film film = new Film(1, "Babai", "Skazka", 120, LocalDate.of(2000, 10, 10), new Motion(1, "G"));
        Film film1 = new Film(2, "Ali", "Skazka", 200, LocalDate.of(2001, 10, 10), new Motion(1, "G"));
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        User user1 = new User(2, "Treant", "Fedia", "@gmail", LocalDate.of(2022, 12,12));
        User user2 = new User(3, "Happy", "Ivan", "@yandex", LocalDate.of(2022, 12,12));
        userDbStorage.addUser(user);
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        filmDbStorage.addFilm(film);
        filmDbStorage.addFilm(film1);
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        filmDbStorage.addLike(2, 3);
        filmDbStorage.addLike(2, 1);

        filmDbStorage.deleteLike(1, 1);
        List<Film> checkList = filmDbStorage.getPopular(1);

        Assertions.assertThat(checkList)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator().isEqualTo(new ArrayList<>(List.of(film1)));

        Assertions.assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {
                    filmDbStorage.deleteLike(1, 5);
                }).withMessage("Фильм или пользователь не найдены");
    }

    @DirtiesContext
    @Test
    public void testGetPopularFilms() {
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        Film film = new Film(1, "Babai", "Skazka", 120, LocalDate.of(2000, 10, 10), new Motion(1, "G"));
        Film film1 = new Film(2, "Ali", "Skazka", 200, LocalDate.of(2001, 10, 10), new Motion(1, "G"));
        User user = new User(1, "Moon", "Ivan", "@mail", LocalDate.of(2022, 12, 12));
        User user1 = new User(2, "Treant", "Fedia", "@gmail", LocalDate.of(2022, 12,12));
        User user2 = new User(3, "Happy", "Ivan", "@yandex", LocalDate.of(2022, 12,12));
        userDbStorage.addUser(user);
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        filmDbStorage.addFilm(film);
        filmDbStorage.addFilm(film1);
        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);
        filmDbStorage.addLike(2, 3);

        List<Film> checkList = filmDbStorage.getPopular(null);
        List<Film> mostPopularFilm = filmDbStorage.getPopular(1);

        Assertions.assertThat(checkList)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator().isEqualTo(new ArrayList<>(List.of(film, film1)));

        Assertions.assertThat(mostPopularFilm)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator().isEqualTo(new ArrayList<>(List.of(film)));
    }
}
