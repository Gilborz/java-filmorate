package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@EqualsAndHashCode (exclude = {"id"})
public class User {
    private Integer id;
    @NotBlank
    private String login;
    private String name;
    @Email
    private String email;
    @Past
    private LocalDate birthday;

}
