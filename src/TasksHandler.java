import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TasksHandler implements HttpHandler {
    final private TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String jsonString = "";

        switch (method) {
            case "GET":
                //jsonString = handleGetRequest(exchange);
                break;
            case "POST":
                break;
            case "DELETE":
                break;
            default:
                System.out.println("Неизвестный метод!");
        }

        exchange.sendResponseHeaders(200, 0);

        System.out.println("jsonString");

        try (OutputStream os = exchange.getResponseBody()) {
            os.write("dsfsdfsd".getBytes());
        }
    }

    public String handleGetRequest(HttpExchange exchange) {
        Gson gson = new Gson();
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");

        if (pathSplit.length == 2) {
            return "";
            //return gson.toJson(manager.getAllTasks());
        } else {
            try {
                return "";
                //return gson.toJson(manager.getTaskById(Integer.parseInt(pathSplit[2])));
            } catch (NumberFormatException e) {
                System.out.println("NumberFormatException. Было введено не id!");
            }
        }

        return "";
    }
}
