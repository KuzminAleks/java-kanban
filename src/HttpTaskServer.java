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

            httpServer.start();

            System.out.println("Сервер запущен! Слушает 8080.");
        } catch (IOException e) {
            System.out.println("IOException при запуске сервера!");
        }
    }

    public void stop() {
        httpServer.stop(0);
    }
}
