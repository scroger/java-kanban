package http.handlers;

import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import exceptions.EpicNotSpecifiedException;
import exceptions.NotFoundException;
import exceptions.TaskInstersectsException;
import exceptions.http.BadRequestHttpException;
import exceptions.http.HttpException;
import exceptions.http.NotAcceptableHttpException;
import exceptions.http.NotFoundHttpException;
import model.Task;
import service.TaskManager;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void resolveAndRun(HttpExchange exchange) throws HttpException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();

        if (pathParts.length == 2) {
            if ("GET".equalsIgnoreCase(method)) {
                handleGetAll(exchange);
                return;
            }

            if ("POST".equalsIgnoreCase(method)) {
                handlePost(exchange);
                return;
            }
        }

        if (pathParts.length == 3) {
            Long id = getId(pathParts);

            if ("GET".equalsIgnoreCase(method)) {
                handleGetById(exchange, id);
                return;
            }

            if ("DELETE".equalsIgnoreCase(method)) {
                handleDeleteById(exchange, id);
                return;
            }
        }

        super.resolveAndRun(exchange);
    }

    protected Long getId(String[] pathParts) throws HttpException {
        try {
            return Long.parseLong(pathParts[2]);
        } catch (NumberFormatException e) {
            System.out.println("[getId] " + e.getMessage());

            throw new BadRequestHttpException();
        }
    }

    private void handleGetAll(HttpExchange exchange) {
        sendText(exchange, getGson().toJson(getAll()));
    }

    private void handlePost(HttpExchange exchange) throws HttpException {
        Task task = readJson(exchange);

        try {
            if (null == task.getId()) {
                create(task);
            } else {
                update(task);
            }
        } catch (TaskInstersectsException e) {
            System.out.println("[handlePost] " + e.getMessage());

            throw new NotAcceptableHttpException();
        } catch (NotFoundException | EpicNotSpecifiedException e) {
            System.out.println("[handlePost] " + e.getMessage());

            throw new NotFoundHttpException();
        }

        sendText(exchange, null);
    }

    private void handleGetById(HttpExchange exchange, Long id) throws HttpException {
        try {
            sendText(exchange, getGson().toJson(getById(id)));
        } catch (NotFoundException e) {
            System.out.println("[handleGetTask] " + e.getMessage());

            throw new NotFoundHttpException();
        }
    }

    private void handleDeleteById(HttpExchange exchange, Long id) throws HttpException {
        try {
            deleteById(id);
        } catch (NotFoundException e) {
            System.out.println("[handleDeleteTask] " + e.getMessage());

            throw new NotFoundHttpException();
        }

        sendText(exchange, null);
    }

    protected List<? extends Task> getAll() {
        return getTaskManager().getTasks();
    }

    protected void create(Task task) {
        getTaskManager().createTask(task);
    }

    protected void update(Task task) {
        getTaskManager().updateTask(task);
    }

    protected Task getById(Long id) {
        return getTaskManager().getTask(id);
    }

    protected void deleteById(Long id) {
        getTaskManager().deleteTask(id);
    }

    protected Task readJson(HttpExchange exchange) throws HttpException {
        Task task = readJson(exchange, Task.class);

        if (null != task.getId()) {
            if (null != task.getStartTime() && null != task.getDuration()) {
                return new Task(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(),
                        task.getStartTime(), task.getDuration());
            }

            return new Task(task.getId(), task.getTitle(), task.getDescription(), task.getStatus());
        }

        if (null != task.getStartTime() && null != task.getDuration()) {
            return new Task(task.getTitle(), task.getDescription(), task.getStartTime(), task.getDuration());
        }

        return new Task(task.getTitle(), task.getDescription());
    }

}
