package http.handlers;

import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import exceptions.http.HttpException;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class SubtaskHandler extends TaskHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected List<? extends Task> getAll() {
        return getTaskManager().getSubtasks();
    }

    @Override
    protected void create(Task task) {
        getTaskManager().createSubtask((Subtask) task);
    }

    @Override
    protected void update(Task task) {
        getTaskManager().updateSubtask((Subtask) task);
    }

    @Override
    protected Task getById(Long id) {
        return getTaskManager().getSubtask(id);
    }

    @Override
    protected void deleteById(Long id) {
        getTaskManager().deleteSubtask(id);
    }

    @Override
    protected Subtask readJson(HttpExchange exchange) throws HttpException {
        Subtask subtask = readJson(exchange, Subtask.class);

        if (null != subtask.getId()) {
            if (null != subtask.getStartTime() && null != subtask.getDuration()) {
                return new Subtask(subtask.getId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus(),
                        subtask.getEpicId(), subtask.getStartTime(), subtask.getDuration());
            }

            return new Subtask(subtask.getId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus(),
                    subtask.getEpicId());
        }

        if (null != subtask.getStartTime() && null != subtask.getDuration()) {
            return new Subtask(subtask.getTitle(), subtask.getDescription(), subtask.getEpicId(),
                    subtask.getStartTime(), subtask.getDuration());
        }

        return new Subtask(subtask.getTitle(), subtask.getDescription(), subtask.getEpicId());
    }

}
