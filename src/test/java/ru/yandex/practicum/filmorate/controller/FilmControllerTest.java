package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setFilmController() {
        filmController = new FilmController();
    }

    @Test
    void addFilmIfDateHisBeforeBirthdayFilm() {
        LocalDate localDate = LocalDate.of(1800, 10, 23);
        Film film = new Film(1, "film", "desc", localDate, 100);

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void updateFilmWithUnknownId() throws ValidationException {
        LocalDate localDate = LocalDate.of(1900, 10, 23);
        Film film = new Film(1, "film", "desc", localDate, 100);
        Film film1 = new Film(99, "film", "desc", localDate, 100);

        filmController.addFilm(film);

        assertThrows(Throwable.class, () -> filmController.updateFilm(film1));
    }

    @Test
    void addFilmIfHisNameEmpty() throws IOException, InterruptedException {
        LocalDate localDate = LocalDate.of(1900, 10, 23);
        Film film = new Film(1, null, "desc", localDate, 100);

        final int status = httpClientPost(film);

        assertEquals(415, status);
    }

    @Test
    void addFilmWithDescriptionMore200() throws IOException, InterruptedException {
        LocalDate localDate = LocalDate.of(1900, 10, 23);
        Film film = new Film(1, "film", "Весной 1942 года по ленинградским улицам" +
                " медленно шли две девочки — Нюра и Рая Ивановы. Впервые после долгой блокадной" +
                " зимы oни отправились пешком с Петроградской стороны на Невский проспект, ко Дворцу пионеров." +
                " Они обходили перевёрнутые трамваи, прятались от взрывов в подворотнях," +
                " пробирались по грудам развалин на тротуарах. 3имой девочки похоронили мать, " +
                " умершую от голода, и остались одни в закопчённой квартире с обледеневшими стенами." +
                " Чтобы согреться, сжигали мебель, одежду, книги.", localDate, 100);

        final int status = httpClientPost(film);

        assertEquals(415, status);
    }

    @Test
    void addFilmWithNegativeDuration() throws IOException, InterruptedException {
        LocalDate localDate = LocalDate.of(1900, 10, 23);
        Film film = new Film(1, "film", "desc", localDate, -100);

        final int status = httpClientPost(film);

        assertEquals(415, status);
    }

    public int httpClientPost(Film film) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/films");
        Gson gson = new Gson();
        String json = gson.toJson(film);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode();
    }
}
