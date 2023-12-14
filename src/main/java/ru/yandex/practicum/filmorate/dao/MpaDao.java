package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Motion;
import java.util.List;

@Component
public interface MpaDao {

    public List<Motion> getAllMpa();

    public Motion getMpaById(Integer id) throws ValidationException;
}
