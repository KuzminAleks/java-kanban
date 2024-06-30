package http.handlers;

import managers.interfaces.TaskManager;
import tasks.task.Task;
import custom.format.adapters.FormatAdapters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public class PrioritizedHandle extends FormatAdapters implements HttpHandler {
    final TaskManager manager;

    final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new FormatAdapters.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new FormatAdapters.DurationAdapter())
            .create();

    public PrioritizedHandle(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String jsonAnswer;

        if (method.equals("GET")) {
            jsonAnswer = gson.toJson(handleGetRequestGetPrioritized());
            super.sendText(exchange, jsonAnswer);
        } else {
            System.out.println("Неизвестный метод!");
        }
    }

    public Set<Task> handleGetRequestGetPrioritized() {
        return manager.getPrioritizedTasks();
    }
}
