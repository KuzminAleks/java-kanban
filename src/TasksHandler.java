import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TasksHandler implements HttpHandler {
    final private TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    class TaskListTypeToken extends TypeToken<ArrayList<Task>> {
        // здесь ничего не нужно реализовывать
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println(exchange.getRequestHeaders());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        String method = exchange.getRequestMethod();
        String jsonString = "";
        String jsonAnswer = "";

        switch (method) {
            case "GET":
                //List<Task> tempList = handleGetRequest(exchange);
                //jsonAnswer = gson.toJson(tempList);
                break;
            case "POST":
                break;
            case "DELETE":
                break;
            default:
                System.out.println("Неизвестный метод!");
        }

        exchange.sendResponseHeaders(200, 0);

        System.out.println(jsonString);
        System.out.println(jsonAnswer);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonString.getBytes());
        }
    }

    public List<Task> handleGetRequest(HttpExchange exchange) {
        //Gson gson = new Gson();
        String[] pathSplit = exchange.getRequestURI().getPath().split("/");

        if (pathSplit.length == 2) {
            return manager.getAllTasks();
        } else {
            try {
                //return manager.getTaskById(Integer.parseInt(pathSplit[2])).toString();
            } catch (NumberFormatException e) {
                System.out.println("NumberFormatException. Было введено не id!");
            }
        }

        return null;
    }
}
