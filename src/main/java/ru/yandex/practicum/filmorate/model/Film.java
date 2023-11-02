package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Integer duration;

}
