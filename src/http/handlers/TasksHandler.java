package http.handlers;

import custom.format.adapters.FormatAdapters;
import managers.interfaces.TaskManager;
import tasks.task.Task;
import task.enums.TaskStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class TasksHandler extends FormatAdapters implements HttpHandler {
    final TaskManager manager;

    final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String jsonAnswer;

        switch (method) {
            case "GET":
                if (exchange.getRequestURI().getPath().split("/").length > 2) {
                    Optional<Task> task = handleGetRequestTaskById(exchange);

                    if (task.isPresent()) {
                        jsonAnswer = gson.toJson(task.get());

                        super.sendText(exchange, jsonAnswer);
                    } else {
                        super.sendNotFound(exchange, "Задача не найдена!");
                    }
                } else {
                    jsonAnswer = gson.toJson(handleGetRequestAllTasks());

                    super.sendText(exchange, jsonAnswer);
                }

                break;
            case "POST":
                handlePostRequestAddTask(exchange);

                break;
            case "DELETE":
                handleDeleteRequestById(exchange);
                break;
            default:
                System.out.println("Неизвестный метод!");
        }
    }

    public Optional<Task> handleGetRequestTaskById(HttpExchange exchange) {
        String id = exchange.getRequestURI().getPath().split("/")[2];

        return Optional.ofNullable(manager.getTaskById(Integer.parseInt(id)));
    }

    public List<Task> handleGetRequestAllTasks() {
        return manager.getAllTasks();
    }

    public void handlePostRequestAddTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();

        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (body.contains("idTask")) {
            String idTask = jsonObject.get("idTask").getAsString();
            String name = jsonObject.get("taskName").getAsString();
            String description = jsonObject.get("description").getAsString();
            String taskStatus = jsonObject.get("taskStatus").getAsString();
            String duration = jsonObject.get("duration").getAsString();
            String startTime = jsonObject.get("startTime").getAsString();

            boolean isAdded = manager.updateTask(new Task(name, description, TaskStatus.valueOf(taskStatus), Duration.parse(duration),
                    LocalDateTime.parse(startTime)), Integer.parseInt(idTask));

            if (isAdded) {
                super.sendSuccessCreated(exchange, "Успешно обновлен!");
            } else {
                super.sendHasInteractions(exchange, "Задача пересекается с существующей.");
            }
        } else {
            String name = jsonObject.get("taskName").getAsString();
            String description = jsonObject.get("description").getAsString();
            String taskStatus = jsonObject.get("taskStatus").getAsString();
            String duration = jsonObject.get("duration").getAsString();
            String startTime = jsonObject.get("startTime").getAsString();

            int isAdded = manager.addTask(new Task(name, description, TaskStatus.valueOf(taskStatus), Duration.parse(duration),
                    LocalDateTime.parse(startTime)));

            if (isAdded != -1) {
                super.sendSuccessCreated(exchange, "Успешно добавлен!");
            } else {
                super.sendHasInteractions(exchange, "Задача пересекается с существующей.");
            }
        }
    }

    public void handleDeleteRequestById(HttpExchange exchange) throws IOException {
        String id = exchange.getRequestURI().getPath().split("/")[2];

        if (manager.deleteTaskById(Integer.parseInt(id))) {
            super.sendSuccessDeleted(exchange, "Задача успешно удалена!");
        } else {
            super.sendNotFound(exchange, "Задача не была удалена!");
        }
    }
}
