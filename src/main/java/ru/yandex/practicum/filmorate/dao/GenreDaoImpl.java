package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenre() {
        String query = "SELECT * FROM genre";

        log.info("Выдан список жанров");
        return jdbcTemplate.query(query, genreRowMapper());
    }

    @Override
    public Genre getGenreById(Integer id) throws ValidationException {
        String query = "SELECT * FROM genre WHERE id = ?";

        try {
            log.info("Жанр по id {} отправлен", id);
            return jdbcTemplate.queryForObject(query, genreRowMapper(), id);
        } catch (DataAccessException e) {
            log.info("Жанра с таким id {} нет", id);
            throw new ValidationException("Жанра с таким id " + id + " нет");
        }
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> (new Genre(rs.getInt("id"), rs.getString("genre")));
    }
}
