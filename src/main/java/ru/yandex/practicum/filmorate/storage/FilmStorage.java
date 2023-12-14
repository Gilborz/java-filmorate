package ru.yandex.practicum.filmorate.storage;

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

    public Map<Integer, Film> getFilms();
}
