package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Motion {

    private Integer id;
    private String name;

    public Motion(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
