package service;

import java.util.List;
import java.util.TreeSet;

import model.Epic;
import model.Subtask;
import model.Task;

public interface TaskManager {

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

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

    TreeSet<Task> getPrioritizedTasks();

}
