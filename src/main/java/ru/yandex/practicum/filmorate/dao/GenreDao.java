package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

@Component
public interface GenreDao {

    public List<Genre> getAllGenre();

    public Genre getGenreById(Integer id) throws ValidationException;
}
