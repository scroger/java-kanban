package http.handlers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import exceptions.http.BadRequestHttpException;
import exceptions.http.HttpException;
import exceptions.http.InternalServerErrorHttpException;
import exceptions.http.NotFoundHttpException;
import service.TaskManager;

public abstract class BaseHttpHandler implements HttpHandler {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public final void handle(HttpExchange exchange) {
        try {
            resolveAndRun(exchange);
        } catch (HttpException e) {
            handleHttpException(exchange, e);
        } catch (Exception e) {
            System.out.println("Internal Server Error: " + e.getMessage());

            handleHttpException(exchange, new InternalServerErrorHttpException());
        }
    }

    protected void resolveAndRun(HttpExchange exchange) throws HttpException {
        throw new NotFoundHttpException();
    }

    protected <T> T readJson(HttpExchange exchange, Class<T> tClass) throws HttpException {
        try {
            return getGson().fromJson(new InputStreamReader(exchange.getRequestBody(), DEFAULT_CHARSET), tClass);
        } catch (JsonParseException e) {
            System.out.println("[readJsonBody] " + e.getMessage());

            throw new BadRequestHttpException();
        }
    }

    protected final void sendText(HttpExchange exchange, String body) {
        int code = 201;
        if (null != body) {
            code = 200;
        }

        sendResponse(exchange, code, body);
    }

    protected final void handleHttpException(HttpExchange exchange, HttpException exception) {
        sendResponse(exchange, exception.getStatusCode(), exception.getMessage());
    }

    private void sendResponse(HttpExchange exchange, int code, String body) {
        try (OutputStream os = exchange.getResponseBody()) {
            byte[] resp = null != body ? body.getBytes(DEFAULT_CHARSET) : new byte[0];

            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(code, resp.length);

            if (resp.length > 0) {
                os.write(resp);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    protected TaskManager getTaskManager() {
        return taskManager;
    }

    protected Gson getGson() {
        return gson;
    }

}
