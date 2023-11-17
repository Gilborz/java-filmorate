package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
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
    private Set<Integer> likes;

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
    }

    public void setLike(Integer like) {
        if (likes.contains(like)) {
            log.info("Лайк уже был добавлен");
            return;
        }

        likes.add(like);
        log.info("Лайк добавлен");
    }

    public void removeLike(Integer like) {
        if (likes.contains(like)) {
            log.info("Лайк отсутствует");
            return;
        }

        likes.remove(like);
        log.info("Лайк удалён");
    }
}
