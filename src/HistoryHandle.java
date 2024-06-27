import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryHandle extends FormatAdapters implements HttpHandler {
    final TaskManager manager;

    final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new FormatAdapters.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new FormatAdapters.DurationAdapter())
            .create();

    public HistoryHandle(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String jsonAnswer;

        if (method.equals("GET")) {
            jsonAnswer = gson.toJson(handleGetRequestGetHistory());
            super.sendText(exchange, jsonAnswer);
        } else {
            System.out.println("Неизвестный метод!");
        }
    }

    public List<Task> handleGetRequestGetHistory() {
        return manager.getHistory();
    }
}
