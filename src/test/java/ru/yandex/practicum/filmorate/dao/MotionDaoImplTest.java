package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.SQlDataException;
import ru.yandex.practicum.filmorate.model.Motion;
import java.util.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MotionDaoImplTest {

    private final JdbcTemplate jdbcTemplate;

    private MpaDaoImpl mpaDao;

    @BeforeEach
    public void setUp() {
        mpaDao = new MpaDaoImpl(jdbcTemplate);
    }

    @Test
    public void testGetAllMotion() {
        List<Motion> checkList = mpaDao.getAllMpa();

        Assertions.assertThat(checkList)
                .isNotNull()
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(new ArrayList<>(List.of(
                        new Motion(1, "G"),
                        new Motion(2, "PG"),
                        new Motion(3, "PG-13"),
                        new Motion(4, "R"),
                        new Motion(5, "NC-17")))
                );
    }

    @Test
    public void testGetGenreById() {
        Motion motion = mpaDao.getMpaById(1);

        Assertions.assertThat(motion)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(new Motion(1, "G"));

        Assertions.assertThatExceptionOfType(SQlDataException.class)
                .isThrownBy(() -> {mpaDao.getMpaById(8);
                }).withMessage("Рейтинга с таким id " + 8 + " нет");
    }
}
