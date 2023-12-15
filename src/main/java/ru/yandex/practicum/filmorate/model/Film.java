package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Data
@EqualsAndHashCode(exclude = {"id"})
public class Film {
    private Integer id;
    @NotBlank(message = "Поле имени не должно быть пустым")
    @NotNull(message = "Поле имени не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше нуля")
    private Integer duration;
    private Motion mpa;
    private List<Genre> genres;

    public Film(Integer id, String name, String description, Integer duration, LocalDate releaseDate, Motion mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }
}
