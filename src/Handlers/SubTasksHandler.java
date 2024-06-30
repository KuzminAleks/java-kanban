package Handlers;

import Managers.TaskManager;
import Tasks.Epic;
import Tasks.SubTask;
import Enums.TaskStatus;
import CustomFormatAdapters.FormatAdapters;
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

public class SubTasksHandler extends FormatAdapters implements HttpHandler {
    final TaskManager manager;

    final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new FormatAdapters.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new FormatAdapters.DurationAdapter())
            .registerTypeAdapter(SubTask.class, new FormatAdapters.SubTaskTypeAdapter(new FormatAdapters.EpicTypeAdapter()))
            .create();

    public SubTasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String jsonAnswer;

        switch (method) {
            case "GET":
                if (exchange.getRequestURI().getPath().split("/").length > 2) {
                    Optional<SubTask> subTask = handleGetRequestSubTaskById(exchange);

                    if (subTask.isPresent()) {
                        jsonAnswer = gson.toJson(subTask.get());

                        super.sendText(exchange, jsonAnswer);
                    } else {
                        super.sendNotFound(exchange, "Подзадача не найдена!");
                    }
                } else {
                    jsonAnswer = gson.toJson(handleGetRequestGetAllSubTasks());

                    super.sendText(exchange, jsonAnswer);
                }
                break;
            case "POST":
                handlePostRequestAddSubTask(exchange);
                break;
            case "DELETE":
                handleDeleteRequestSubTaskById(exchange);
                break;
            default:
                System.out.println("Неизвестный метод!");
        }
    }

    public List<SubTask> handleGetRequestGetAllSubTasks() {
        return manager.getAllSubTasks();
    }

    public Optional<SubTask> handleGetRequestSubTaskById(HttpExchange exchange) {
        String id = exchange.getRequestURI().getPath().split("/")[2];

        return Optional.ofNullable(manager.getSubTaskById(Integer.parseInt(id)));
    }

    public void handlePostRequestAddSubTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();

        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println(body);

        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (body.contains("idSubTask")) {
            String idTask = jsonObject.get("idSubTask").getAsString();
            String name = jsonObject.get("taskName").getAsString();
            String description = jsonObject.get("description").getAsString();
            String subTaskStatus = jsonObject.get("taskStatus").getAsString();
            String duration = jsonObject.get("duration").getAsString();
            String startTime = jsonObject.get("startTime").getAsString();

            boolean isAdded = manager.updateSubTask(new SubTask(name, description, TaskStatus.valueOf(subTaskStatus), Duration.parse(duration),
                    LocalDateTime.parse(startTime)), Integer.parseInt(idTask));

            if (isAdded) {
                super.sendSuccessCreated(exchange, "Успешно обновлен!");
            } else {
                super.sendHasInteractions(exchange, "Подзадача пересекается с существующей.");
            }
        } else {
            String epicName = jsonObject.get("epicName").getAsString();
            String epicDescription = jsonObject.get("epicDescription").getAsString();

            String name = jsonObject.get("taskName").getAsString();
            String description = jsonObject.get("description").getAsString();
            String subTaskStatus = jsonObject.get("taskStatus").getAsString();
            String duration = jsonObject.get("duration").getAsString();
            String startTime = jsonObject.get("startTime").getAsString();

            int isAdded = manager.addSubTask(new Epic(epicName, epicDescription), new SubTask(name, description, TaskStatus.valueOf(subTaskStatus), Duration.parse(duration),
                    LocalDateTime.parse(startTime)));

            if (isAdded != -1) {
                super.sendSuccessCreated(exchange, "Подзадача успешно добавлена!");
            } else {
                super.sendHasInteractions(exchange, "Подзадача пересекается с существующей.");
            }
        }
    }

    public void handleDeleteRequestSubTaskById(HttpExchange exchange) throws IOException {
        String id = exchange.getRequestURI().getPath().split("/")[2];

        if (manager.deleteSubTaskById(Integer.parseInt(id))) {
            super.sendSuccessDeleted(exchange, "Подзадача успешно удалена!");
        } else {
            super.sendNotFound(exchange, "Не был удален!");
        }
    }
}
