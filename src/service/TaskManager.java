package service;

import java.util.Collection;
import java.util.List;

import model.Epic;
import model.Subtask;
import model.Task;

public interface TaskManager {

    Collection<Task> getTasks();

    Collection<Epic> getEpics();

    Collection<Subtask> getSubtasks();

    Task getTask(Long id);

    Epic getEpic(Long id);

    Subtask getSubtask(Long id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    void deleteTask(Long id);

    void deleteEpic(Long id);

    void deleteSubtask(Long id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    List<Subtask> getEpicSubtasks(Epic epic);

    List<Task> getHistory();

}
