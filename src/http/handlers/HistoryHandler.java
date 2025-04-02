package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import service.TaskManager;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void resolveAndRun(HttpExchange exchange) {
        sendText(exchange, getGson().toJson(getTaskManager().getHistory()));
    }

}
