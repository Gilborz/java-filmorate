package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode (exclude = {"id"})
public class User {
    private Integer id;
    @NotBlank
    private String login;
    private String name;
    @Email
    @NotNull
    private String email;
    @Past
    private LocalDate birthday;
}
