import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class BaseHttpHandler {
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    protected void sendSuccessCreated(HttpExchange exchange, String text) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(201, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    protected void sendSuccessDeleted(HttpExchange exchange, String text) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(202, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        exchange.sendResponseHeaders(404, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        exchange.sendResponseHeaders(406, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }
}
