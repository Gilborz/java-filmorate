package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

@RestController
public class GenreController {

    private final GenreDao genreDao;

    @Autowired
    public GenreController(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return genreDao.getAllGenre();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable(required = false) Integer id) throws ValidationException {
        return genreDao.getGenreById(id);
    }
}
