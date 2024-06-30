import FormatAdapters.FormatAdapters;
import HttpServer.HttpTaskServer;
import Managers.InMemoryTaskManager;
import Managers.TaskManager;
import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import Enums.TaskStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
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
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest extends FormatAdapters {
    static class TaskListTypeToken extends TypeToken<List<Task>> {
    }

    static class EpicListTypeToken extends TypeToken<List<Epic>> {
    }

    static class SubTasksListTypeToken extends TypeToken<List<SubTask>> {
    }

    static class TaskSetTypeToken extends TypeToken<Set<Task>> {
    }

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

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
        manager.addTask(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));
        manager.addTask(new Task("2 Tasks.Task", "Some description 2", TaskStatus.DONE,
                Duration.ofMinutes(60), LocalDateTime.of(2024, 1, 10, 15, 23, 45)));

        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> taskList;

        try {
            taskList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

            assertEquals(taskList.size(), 2);

            assertArrayEquals(manager.getAllTasks().toArray(), taskList.toArray());
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        Task task = new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int id = manager.addTask(task);


        URI url = URI.create("http://localhost:8080/tasks/" + (id - 1));

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        try {
            Task responseTask = gson.fromJson(response.body(), Task.class);

            assertEquals(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()), responseTask);
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldReturnCode404GetTask() throws IOException, InterruptedException {
        manager.addTask(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));

        URI url = URI.create("http://localhost:8080/tasks/21232");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача не найдена!", response.body());
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");

        String newTask = gson.toJson(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newTask))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now()), manager.getAllTasks().getFirst());
    }

    @Test
    void shouldReturn406AddTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");

        manager.addTask(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));

        String newTask = gson.toJson(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newTask))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals("Задача пересекается с существующей.", response.body());
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");

        int id = manager.addTask(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now())) - 1;

        String newTask = gson.toJson(new Task("Updated Tasks.Task", "Some new description",
                TaskStatus.DONE, Duration.ofMinutes(70), LocalDateTime.of(2023, 12, 5, 14, 32, 33)));

        String newTaskWithId = "{\"idTask\":" + id + "," + newTask.substring(1);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newTaskWithId))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(new Task("Updated Tasks.Task", "Some new description",
                TaskStatus.DONE, Duration.ofMinutes(70), LocalDateTime.of(2023, 12, 5, 14, 32, 33)), manager.getTaskById(id));
    }

    @Test
    void shouldReturn406UpdateTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");

        int id = manager.addTask(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now())) - 1;

        String newTask = gson.toJson(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));

        String newTaskWithId = "{\"idTask\":" + id + "," + newTask.substring(1);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newTaskWithId))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals("Задача пересекается с существующей.", response.body());
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        int id = manager.addTask(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now())) - 1;

        URI url = URI.create("http://localhost:8080/tasks/" + id);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode());
        assertEquals(manager.getAllTasks().size(), 0);
    }

    @Test
    void shouldReturn404DeleteTask() throws IOException, InterruptedException {
        manager.addTask(new Task("1 Tasks.Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));

        URI url = URI.create("http://localhost:8080/tasks/741258");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Задача не была удалена!", response.body());
    }

    @Test
    void getAllEpic() throws IOException, InterruptedException {
        manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1"));
        manager.addEpicTask(new Epic("Tasks.Epic 2", "Some description 2"));

        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        assertEquals(200, response.statusCode());

        List<Epic> epicList;

        try {
            epicList = gson.fromJson(response.body(), new EpicListTypeToken().getType());

            assertEquals(epicList.size(), 2);

            assertArrayEquals(manager.getAllEpicTasks().toArray(), epicList.toArray());
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        int id = manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1")) - 1;

        URI url = URI.create("http://localhost:8080/epics/" + id);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        try {
            Epic responseEpic = gson.fromJson(response.body(), Epic.class);

            assertEquals(new Epic("Tasks.Epic 1", "Some description 1"), responseEpic);
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldReturnCode404GetEpic() throws IOException, InterruptedException {
        manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1"));

        URI url = URI.create("http://localhost:8080/epics/21232");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Эпик не найден!", response.body());
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");

        String newEpic = gson.toJson(new Epic("Tasks.Epic 1", "Some description 1"));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newEpic))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(new Epic("Tasks.Epic 1", "Some description 1"), manager.getAllEpicTasks().getFirst());
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        int id = manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1")) - 1;

        URI url = URI.create("http://localhost:8080/epics/" + id);

        String newEpic = gson.toJson(new Epic("Tasks.Epic UPDATED", "Some description UPDATED"));

        String newEpicWithId = "{\"idEpic\":" + id + "," + newEpic.substring(1);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newEpicWithId))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpicTasks().size());
        assertEquals(new Epic("Tasks.Epic UPDATED", "Some description UPDATED"),
                manager.getEpicById(id));
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        int id = manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1")) - 1;

        URI url = URI.create("http://localhost:8080/epics/" + id);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode());
        assertTrue(manager.getAllEpicTasks().isEmpty());
    }

    @Test
    void shouldReturn404DeleteEpic() throws IOException, InterruptedException {
        manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1"));

        URI url = URI.create("http://localhost:8080/epics/7001");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Эпик не был удален!", response.body());
    }

    @Test
    void getAllSubtasksOfEpic() {
        int id = manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1")) - 1;

        manager.addSubTask(new Epic("Tasks.Epic 1", "Some description 1"),
                new SubTask("Tasks.SubTask 1 of epic 1", "Description", TaskStatus.NEW,
                        Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0))
        );
        manager.addSubTask(new Epic("Tasks.Epic 1", "Some description 1"),
                new SubTask("Tasks.SubTask 2 of epic 1", "Description", TaskStatus.NEW,
                        Duration.ofMinutes(12), LocalDateTime.now())
        );

        URI url = URI.create("http://localhost:8080/epics/" + id + "/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

            assertEquals(200, response.statusCode());

            List<SubTask> subTasksList;

            try {
                subTasksList = gson.fromJson(response.body(), new SubTasksListTypeToken().getType());

                assertEquals(subTasksList.size(), 2);

                assertArrayEquals(manager.getAllSubTasks().toArray(), subTasksList.toArray());
            } catch (JsonIOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAllSubTask() throws IOException, InterruptedException {
        manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1"));

        manager.addSubTask(new Epic("Tasks.Epic 1", "Some description 1"),
                new SubTask("Tasks.SubTask 1 of epic 1", "Description", TaskStatus.NEW,
                        Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0))
        );
        manager.addSubTask(new Epic("Tasks.Epic 1", "Some description 1"),
                new SubTask("Tasks.SubTask 2 of epic 1", "Description", TaskStatus.NEW,
                        Duration.ofMinutes(12), LocalDateTime.now())
        );

        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        assertEquals(200, response.statusCode());

        List<SubTask> subTaskList = gson.fromJson(response.body(), new SubTasksListTypeToken().getType());

        assertEquals(2, subTaskList.size());
    }

    @Test
    void getSubTaskById() throws IOException, InterruptedException {
        manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1"));

        int id = manager.addSubTask(new Epic("Tasks.Epic 1", "Some description 1"),
                new SubTask("Tasks.SubTask 1 of epic 1", "Description", TaskStatus.NEW,
                        Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0))
        ) - 1;

        URI url = URI.create("http://localhost:8080/subtasks/" + id);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        SubTask subTask = gson.fromJson(response.body(), SubTask.class);

        assertEquals(new SubTask("Tasks.SubTask 1 of epic 1", "Description", TaskStatus.NEW,
                Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0)), subTask);
    }

    @Test
    void addSubTask() throws IOException, InterruptedException {
        manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1"));

        String newSubTask = "{\"epicName\":\"Tasks.Epic 1\",\"epicDescription\":\"Some description 1\"," + gson.toJson(new SubTask("Tasks.SubTask 1 of epic 1", "Description", TaskStatus.NEW,
                Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0))).substring(1);

        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newSubTask))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Подзадача успешно добавлена!", response.body());

    }

    @Test
    void updateSubTask() throws IOException, InterruptedException {
        manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1"));

        int id = manager.addSubTask(new Epic("Tasks.Epic 1", "Some description 1"),
                new SubTask("Tasks.SubTask 1 of epic 1", "Description", TaskStatus.NEW,
                        Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0))
        ) - 1;

        String newSubTask = "{\"idSubTask\":" + id + "," + gson.toJson(new SubTask("Tasks.SubTask 1 of epic 1 UPDATED", "Description UPDATED", TaskStatus.DONE,
                Duration.ofMinutes(12), LocalDateTime.of(2023, 12, 31, 13, 0))).substring(1);

        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(newSubTask))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        assertEquals(new SubTask("Tasks.SubTask 1 of epic 1 UPDATED", "Description UPDATED",
                        TaskStatus.DONE, Duration.ofMinutes(12),
                        LocalDateTime.of(2023, 12, 31, 13, 0)),
                        manager.getSubTaskById(id));
    }

    @Test
    void deleteSubTaskById() throws IOException, InterruptedException {
        manager.addEpicTask(new Epic("Tasks.Epic 1", "Some description 1"));

        int id = manager.addSubTask(new Epic("Tasks.Epic 1", "Some description 1"),
                new SubTask("Tasks.SubTask 1 of epic 1", "Description", TaskStatus.NEW,
                        Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0))
        ) - 1;


        URI url = URI.create("http://localhost:8080/subtasks/" + id);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(202, response.statusCode());
        assertEquals("Подзадача успешно удалена!", response.body());
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        int id1 = manager.addTask(new Task("Tasks.Task 1", "Description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.now()));
        int id2 = manager.addTask(new Task("Tasks.Task 2", "Description", TaskStatus.DONE,
                Duration.ofMinutes(35), LocalDateTime.of(2024, 6, 14, 15, 0)));

        manager.getTaskById(id2);
        manager.getTaskById(id1);

        URI url = URI.create("http://localhost:8080/history");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> historyList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertArrayEquals(manager.getHistory().toArray(), historyList.toArray());
    }

    @Test
    void getPrioritized() throws IOException, InterruptedException {
        manager.addTask(new Task("Tasks.Task 1", "Description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.now()));
        manager.addTask(new Task("Tasks.Task 2", "Description", TaskStatus.DONE,
                Duration.ofMinutes(35), LocalDateTime.of(2024, 6, 14, 15, 0)));

        URI url = URI.create("http://localhost:8080/prioritized");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Set<Task> taskSet = gson.fromJson(response.body(), new TaskSetTypeToken().getType());

        assertArrayEquals(manager.getPrioritizedTasks().toArray(), taskSet.toArray());
    }
}
