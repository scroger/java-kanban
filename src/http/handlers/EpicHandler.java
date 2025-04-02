package http.handlers;

import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import exceptions.http.HttpException;
import model.Epic;
import model.Task;
import service.TaskManager;

public class EpicHandler extends TaskHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void resolveAndRun(HttpExchange exchange) throws HttpException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();

        if (pathParts.length == 4 && "GET".equalsIgnoreCase(method)) {
            handleGetEpicSubtasks(exchange, getId(pathParts));
            return;
        }

        super.resolveAndRun(exchange);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, Long id) {
        sendText(exchange, getGson().toJson(getTaskManager().getEpicSubtasks((Epic) getById(id))));
    }

    @Override
    protected List<? extends Task> getAll() {
        return getTaskManager().getEpics();
    }

    @Override
    protected void create(Task task) {
        getTaskManager().createEpic((Epic) task);
    }

    @Override
    protected void update(Task task) {
        getTaskManager().updateEpic((Epic) task);
    }

    @Override
    protected Task getById(Long id) {
        return getTaskManager().getEpic(id);
    }

    @Override
    protected void deleteById(Long id) {
        getTaskManager().deleteEpic(id);
    }

    @Override
    protected Epic readJson(HttpExchange exchange) throws HttpException {
        Epic epic = readJson(exchange, Epic.class);

        if (null != epic.getId()) {
            if (null != epic.getEndTime()) {
                return new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStatus(),
                        epic.getEndTime());
            }

            return new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStatus());
        }

        if (null != epic.getEndTime()) {
            return new Epic(epic.getTitle(), epic.getDescription(), epic.getEndTime());
        }

        return new Epic(epic.getTitle(), epic.getDescription());
    }

}
