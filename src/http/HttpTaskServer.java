package http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import http.handlers.EpicHandler;
import http.handlers.HistoryHandler;
import http.handlers.PrioritizedTasksHandler;
import http.handlers.SubtaskHandler;
import http.handlers.TaskHandler;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import service.Managers;
import service.TaskManager;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager, Gson gson) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(taskManager, gson));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) {
        try {
            (new HttpTaskServer(
                    Managers.getDefault(),
                    (new GsonBuilder())
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                            .registerTypeAdapter(Duration.class, new DurationAdapter())
                            .create()
            )).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
