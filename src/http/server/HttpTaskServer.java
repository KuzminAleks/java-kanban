package http.server;

import http.handlers.EpicHandler;
import http.handlers.HistoryHandle;
import http.handlers.PrioritizedHandle;
import http.handlers.SubTasksHandler;
import http.handlers.TasksHandler;
import managers.interfaces.TaskManager;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final TaskManager manager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public static void main(String[] args) {
    }

    public void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

            httpServer.createContext("/tasks", new TasksHandler(manager));
            httpServer.createContext("/epics", new EpicHandler(manager));
            httpServer.createContext("/subtasks", new SubTasksHandler(manager));
            httpServer.createContext("/history", new HistoryHandle(manager));
            httpServer.createContext("/prioritized", new PrioritizedHandle(manager));

            httpServer.start();

            System.out.println("Сервер запущен! Слушает 8080.");
        } catch (IOException e) {
            System.out.println("IOException при запуске сервера!");
        }
    }

    public void stop() {
        httpServer.stop(0);

        System.out.println("Сервер остановлен!");
    }
}
