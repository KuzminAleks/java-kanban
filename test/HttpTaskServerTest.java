import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new Gson();

    public HttpTaskServerTest() throws IOException {
        //taskServer.start();
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpicTasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        //taskServer.check();

        //manager.addTask(new Task("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));



        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                //.timeout(Duration.ofSeconds(10))
                .GET()
                .uri(url)
                //.GET()
                //.header("Accept", "application/json")
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();

        //taskServer.check();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task task = gson.fromJson(response.body(), Task.class);

        assertEquals("1 Task", task.getTaskName());
    }
}
