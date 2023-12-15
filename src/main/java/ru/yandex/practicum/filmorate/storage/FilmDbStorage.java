package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SQlDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Motion;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> someFilms = getListFilms();

        for (Film film : someFilms) {
            film.setGenres(getListGenres(film.getId()));
        }

        log.info("Список всех фильмов найден");
        return someFilms;
    }

    @Override
    public Film addFilm(Film film) {
        saveFilm(film);

        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }

        saveGenre(film.getGenres(), film.getId());
        film.setGenres(getListGenres(film.getId()));

        log.info("Фильм добавлен, id равно {}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws SQlDataException {
        String query = "UPDATE film SET name = ?, description = ?, duration = ?, release_date = ?, mpa = ?\n" +
                "WHERE film_id = ?";

        int rowNum = jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId()
        );

        if (rowNum == 0) {
            log.info("Фильм с id {} не найден", film.getId());
            throw new SQlDataException("Фильм с таким id " + film.getId() + " не найден");
        }

        deleteGenres(film.getId());

        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        } else {
            saveGenre(film.getGenres(), film.getId());
            film.setGenres(getListGenres(film.getId()));
        }

        log.info("Фильм обновлён, id равен {}", film.getId());
        return film;
    }

    @Override
    public Film getFilmById(Integer id) throws ValidationException {
        String query = "SELECT f.film_id, f.name, f.description, f.duration, f.release_date, f.mpa, m.motion\n" +
                "FROM film f\n" +
                "JOIN mpa m ON f.mpa = m.id\n" +
                "WHERE film_id = ?";

        try {
            Film film = jdbcTemplate.queryForObject(query, filmRowMapper(), id);
            film.setGenres(getListGenres(film.getId()));

            log.info("Фильм с id {} отправлен", id);
            return film;
        } catch (DataAccessException e) {
            log.info("Фильм с таким id {} не найден", id);
            throw new ValidationException("Фильм не найден, id равен " + id);
        }
    }

    public void addLike(Integer filmId, Integer userId) throws DataAccessException {
        String query = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(query, filmId, userId);
        } catch (DataAccessException e) {
            throw new SQlDataException("Фильм или пользователь не найдены");
        }
    }

    public void deleteLike(Integer filmId, Integer userId) throws ValidationException {
        String query  = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";

        int rowNum = jdbcTemplate.update(query, userId, filmId);

        log.info("Лайк у фильма с id {} удалён", filmId);

        if (rowNum == 0) {
            log.info("Фильм или пользователь не найдены");
            throw new ValidationException("Фильм или пользователь не найдены");
        }
    }

    public List<Film> getPopular(Integer count) {
        String query = "SELECT f.film_id, f.name, f.description, f.duration, f.release_date, f.mpa, m.motion\n" +
                "FROM film f \n" +
                "LEFT JOIN (SELECT film_id, user_id\n" +
                "      FROM likes) AS l \n" +
                "        ON f.film_id = l.film_id \n" +
                "JOIN mpa m ON f.mpa = m.id\n" +
                "GROUP BY f.film_id, m.motion\n" +
                "ORDER BY COUNT(user_id) DESC\n" +
                "LIMIT 10";

        List<Film> popularFilm = new ArrayList<>(jdbcTemplate.query(query, filmRowMapper()));

        for (Film film : popularFilm) {
            film.setGenres(getListGenres(film.getId()));
        }

        log.info("Список популярных фильмов отправлен");

        if (count == null) {
            return popularFilm;
        }

        return popularFilm.subList(0, count);
    }

    private void saveFilm(Film film) {
        String query = "INSERT INTO film (name, description, duration, release_date, mpa) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(query, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setInt(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setObject(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
    }

    private void saveGenre(List<Genre> genres, Integer idFilm) {
        Set<Genre> set = new HashSet<>(genres);
        List<Genre> setGenre = new ArrayList<>(set);
        Collections.sort(setGenre, (g1, g2) -> g1.getId() - g2.getId());
        if (genres.size() > 0) {
            for (Genre genre : setGenre) {
                String query = "INSERT INTO film_genre_id (film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(query, idFilm, genre.getId());
            }
        }
    }

    private List<Film> getListFilms() {
        String query = "SELECT film_id, name, description, duration, release_date, mpa, motion\n" +
                "FROM film f JOIN mpa m ON f.mpa = m.id";

        return new ArrayList<>(jdbcTemplate.query(query, filmRowMapper()));
    }

    private List<Genre> getListGenres(Integer id) {
        String query = "SELECT genre_id, genre\n" +
                "FROM film_genre_id fgi\n" +
                "JOIN genre g ON fgi.genre_id = g.id WHERE film_id = ?";

        return jdbcTemplate.query(query, genreRowMapper(), id);
    }

    private void deleteGenres(Integer id) {
        String query = "DELETE FROM film_genre_id WHERE film_id = ?";
        jdbcTemplate.update(query, id);
    }

    private RowMapper<Genre> genreRowMapper() {
         return ((rs, rowNum) -> new Genre(
                 rs.getInt("genre_id"),
                 rs.getString("genre"))
         );
    }

    private RowMapper<Film> filmRowMapper() {
        return ((rs, rowNum) -> new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("duration"),
                rs.getDate("release_date").toLocalDate(),
                new Motion(rs.getInt("mpa"), rs.getString("motion")))
        );
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return null;
    }
}
