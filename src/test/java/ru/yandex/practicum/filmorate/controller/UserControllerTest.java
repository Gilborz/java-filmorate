package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUserController() {
        userController = new UserController();
    }

    @Test
    void createUserIfLoginHaveVoidOrEmpty() throws IOException, InterruptedException {
        LocalDate localDate = LocalDate.of(2000,12,12);
        User user = new User(1, "jkl lkj", "Bill", "mail@ru", localDate);

        User user1 = new User(1, null, "Bill", "mail@ru", localDate);

        final int status = httpClientPost(user1);

        assertEquals(415, status);
        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void createUserIfNameEqualsNullThenNameEqualsLogin() throws ValidationException {
        LocalDate localDate = LocalDate.of(2000,12,12);
        User user = new User(1, "jkllkj", null, "mail@ru", localDate);

        final User load = userController.addUser(user);
        final User save = userController.getUsers().get(0);

        assertEquals(save, load);
    }

    @Test
    void updateUserWithUnknownId() throws ValidationException {
        LocalDate localDate = LocalDate.of(2000,12,12);
        User user = new User(1, "jkllkj", "Bill", "mail@ru", localDate);
        User user1 = new User(99, "jkllkj", "Bill", "mail@ru", localDate);

        userController.addUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(user1));
    }

    @Test
    void createUserWithIncorrectEmail() throws IOException, InterruptedException {
        LocalDate localDate = LocalDate.of(2000,12,12);
        User user = new User(1, "jkllkj", "Bill", "ma", localDate);

        final int status = httpClientPost(user);

        assertEquals(415, status);
    }

    @Test
    void createUserIfBirthdayInFuture() throws IOException, InterruptedException {
        LocalDate localDate = LocalDate.of(3000,12,12);
        User user = new User(1, "jkllkj", "Bill", "mail@ru", localDate);

        final int status = httpClientPost(user);

        assertEquals(415, status);
    }

    public int httpClientPost(User user) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/users");
        Gson gson = new Gson();
        String json = gson.toJson(user);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode();
    }
}