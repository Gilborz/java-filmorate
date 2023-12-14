package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Motion;

import java.util.List;

@RestController
public class MotionController {

    private final MpaDao mpaDao;

    @Autowired
    public MotionController(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @GetMapping("/mpa")
    public List<Motion> getAllMotion() {
        return mpaDao.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Motion getMotionById(@PathVariable(required = false) Integer id) throws ValidationException {
        return mpaDao.getMpaById(id);
    }
}
