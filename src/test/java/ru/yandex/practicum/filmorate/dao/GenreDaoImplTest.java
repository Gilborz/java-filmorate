package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.ArrayList;
import java.util.List;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoImplTest {

    private final JdbcTemplate jdbcTemplate;

    private GenreDaoImpl genreDao;

    @BeforeEach
    public void setUp() {
        genreDao = new GenreDaoImpl(jdbcTemplate);
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> checkList = genreDao.getAllGenre();

        Assertions.assertThat(checkList)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(new ArrayList<>(List.of(
                        new Genre(1, "Комедия"),
                        new Genre(2, "Драма"),
                        new Genre(3, "Мультфильм"),
                        new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")))
                );
    }

    @Test
    public void testGetGenreById() throws ValidationException {
        Genre checkGenre = genreDao.getGenreById(1);

        Assertions.assertThat(checkGenre)
                .usingRecursiveComparison()
                .isEqualTo(new Genre(1, "Комедия"));

        Assertions.assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> {genreDao.getGenreById(7);
                }).withMessage("Жанра с таким id " + 7 + " нет");
    }
}
