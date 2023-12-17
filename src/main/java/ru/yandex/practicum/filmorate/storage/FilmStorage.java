package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;

@Component
public interface FilmStorage {

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film) throws ValidationException;

    Film getFilmById(Integer id) throws ValidationException;

    void addLike(Integer filmId, Integer userId) throws DataAccessException;

    void deleteLike(Integer filmId, Integer userId) throws ValidationException;

    List<Film> getPopular(Integer count);

    public Map<Integer, Film> getFilms();
}
