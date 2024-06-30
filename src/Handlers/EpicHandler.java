package Handlers;

import Managers.TaskManager;
import Tasks.Epic;
import Tasks.SubTask;
import FormatAdapters.FormatAdapters;
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

public class EpicHandler extends FormatAdapters implements HttpHandler {
    final TaskManager manager;

    final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new FormatAdapters.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new FormatAdapters.DurationAdapter())
            .registerTypeAdapter(Epic.class, new FormatAdapters.EpicTypeAdapter())
            .create();

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String jsonAnswer;

        switch (method) {
            case "GET":
                if (exchange.getRequestURI().getPath().split("/").length > 2) {
                    if (exchange.getRequestURI().getPath().contains("subtasks")) {
                        jsonAnswer = gson.toJson(handleGetRequestGetAllSubtasks(exchange));

                        super.sendText(exchange, jsonAnswer);
                    } else {
                        Optional<Epic> epic = handleGetRequestEpicById(exchange);

                        if (epic.isPresent()) {
                            jsonAnswer = gson.toJson(epic.get());

                            super.sendText(exchange, jsonAnswer);
                        } else {
                            super.sendNotFound(exchange, "Эпик не найден!");
                        }
                    }
                } else {
                    jsonAnswer = gson.toJson(handleGetRequestGetAllEpic());

                    super.sendText(exchange, jsonAnswer);
                }
                break;
            case "POST":
                handlePostRequestAddEpic(exchange);
                break;
            case "DELETE":
                handleDeleteRequestEpicById(exchange);
                break;
            default:
                System.out.println("Неизвестный метод!");
        }
    }

    public Optional<Epic> handleGetRequestEpicById(HttpExchange exchange) {
        String id = exchange.getRequestURI().getPath().split("/")[2];

        return Optional.ofNullable(manager.getEpicById(Integer.parseInt(id)));
    }

    public List<Epic> handleGetRequestGetAllEpic() {
        return manager.getAllEpicTasks();
    }

    public List<SubTask> handleGetRequestGetAllSubtasks(HttpExchange exchange) {
        String id = exchange.getRequestURI().getPath().split("/")[2];
        return manager.getSubTaskOfEpic(manager.getEpicById(Integer.parseInt(id)));
    }

    public void handlePostRequestAddEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();

        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println(body);

        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (body.contains("idEpic")) {
            String idEpic = jsonObject.get("idEpic").getAsString();
            String name = jsonObject.get("taskName").getAsString();
            String description = jsonObject.get("description").getAsString();

            manager.updateEpicTask(new Epic(name, description), Integer.parseInt(idEpic));

            super.sendSuccessCreated(exchange, "Успешно обновлен!");
        } else {
            String name = jsonObject.get("taskName").getAsString();
            String description = jsonObject.get("description").getAsString();

            manager.addEpicTask(new Epic(name, description));

            super.sendSuccessCreated(exchange, "Успешно добавлен!");
        }
    }

    public void handleDeleteRequestEpicById(HttpExchange exchange) throws IOException {
        String id = exchange.getRequestURI().getPath().split("/")[2];

        if (manager.deleteEpicById(Integer.parseInt(id))) {
            super.sendSuccessDeleted(exchange, "Эпик успешно удален!");
        } else {
            super.sendNotFound(exchange, "Эпик не был удален!");
        }
    }
}
