package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import service.TaskManager;

public class PrioritizedTasksHandler extends BaseHttpHandler {

    public PrioritizedTasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void resolveAndRun(HttpExchange exchange) {
        sendText(exchange, getGson().toJson(getTaskManager().getPrioritizedTasks()));
    }

}
