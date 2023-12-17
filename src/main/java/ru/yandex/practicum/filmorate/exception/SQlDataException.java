package ru.yandex.practicum.filmorate.exception;

import org.springframework.dao.DataAccessException;

public class SQlDataException extends DataAccessException {

    public SQlDataException(String message) {
        super(message);
    }
}
