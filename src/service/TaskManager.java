package service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class TaskManager {

    private long taskIdCounter;
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Epic> epics = new HashMap<>();
    private final Map<Long, Subtask> subtasks = new HashMap<>();

    private long generateId() {
        return ++taskIdCounter;
    }

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public Collection<Epic> getEpics() {
        return epics.values();
    }

    public Collection<Subtask> getSubtasks() {
        return subtasks.values();
    }

    public Task getTask(long taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpic(long epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtask(long subtaskId) {
        return subtasks.get(subtaskId);
    }

    public Task createTask(Task task) {
        task.setId(generateId());

        if (TaskStatus.NEW != task.getStatus()) {
            task.setStatus(TaskStatus.NEW);
        }

        tasks.put(task.getId(), task);

        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epic.setStatus(TaskStatus.NEW);

        epics.put(epic.getId(), epic);

        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtask.setStatus(TaskStatus.NEW);

        if (null == subtask.getEpicId()) {
            System.out.println("No epic specified");

            return null;
        }

        Epic epic = getEpic(subtask.getEpicId());
        if (null == epic) {
            System.out.println("Epic with id=" + subtask.getEpicId() + " not found");

            return null;
        }

        subtasks.put(subtask.getId(), subtask);

        epic.addSubtask(subtask.getId());
        epic.setStatus(calculateEpicStatus(epic));

        return subtask;
    }

    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Task with id=" + task.getId() + " not found");

            return null;
        }

        tasks.put(task.getId(), task);

        return task;
    }

    public Epic updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Epic with id=" + epic.getId() + " not found");

            return null;
        }

        // Восстанавливаем привязанные подзадачи
        Epic oldEpic = getEpic(epic.getId());
        epic.setSubtaskIds(oldEpic.getSubtaskIds());

        // Нельзя менять статус эпика вручную
        if (epic.getStatus() != oldEpic.getStatus()) {
            // Комментарий ревьюера: "Даже если такое придет (taskManager.updateEpic(new Epic(1L, "test", "test", TaskStatus.DONE))),
            //   метод calculateEpicStatus() пересчитает его в правильный статус"
            // Ответ: "Да, пересчитает и выдаст тот же статус что и oldEpic.getStatus(). Не понятно зачем делать лишний пересчет статуса.
            //   Ок, исправил."
            epic.setStatus(calculateEpicStatus(epic));
        }

        epics.put(epic.getId(), epic);

        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Subtask with id=" + subtask.getId() + " not found");

            return null;
        }

        if (null == subtask.getEpicId()) {
            System.out.println("No epic specified");

            return null;
        }

        if (null == getEpic(subtask.getEpicId())) {
            System.out.println("Epic with id=" + subtask.getEpicId() + " not found. Updating subtask failed.");

            return null;
        }

        subtasks.put(subtask.getId(), subtask);

        for (Epic epic : getEpics()) {
            if (epic.getSubtaskIds().contains(subtask.getId())) {
                if (!Objects.equals(epic.getId(), subtask.getEpicId())) {
                    epic.deleteSubtask(subtask.getId());
                }

                epic.setStatus(calculateEpicStatus(epic));
            } else if (Objects.equals(epic.getId(), subtask.getEpicId())) {
                epic.addSubtask(subtask.getId());

                epic.setStatus(calculateEpicStatus(epic));
            }
        }

        return subtask;
    }

    public void deleteTask(long id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Task with id=" + id + " not found");

            return;
        }

        tasks.remove(id);
    }

    public void deleteEpic(long id) {
        Epic epic = getEpic(id);

        if (null == epic) {
            System.out.println("Epic with id=" + id + " not found");

            return;
        }

        for (long subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(id);
    }

    public void deleteSubtask(long id) {
        Subtask subtask = getSubtask(id);
        if (null == subtask) {
            System.out.println("Subtask with id=" + id + " not found");

            return;
        }

        Epic epic = getEpic(subtask.getEpicId());
        if (null != epic) {
            epic.deleteSubtask(id);
            epic.setStatus(calculateEpicStatus(epic));
        }

        subtasks.remove(id);
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        for (Epic epic : getEpics()) {
            for (long subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }

        epics.clear();
    }

    public void deleteSubtasks() {
        for (Epic epic : getEpics()) {
            epic.deleteSubtasks();
            epic.setStatus(TaskStatus.NEW);
        }

        subtasks.clear();
    }

    public List<Subtask> getEpicTasks(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();

        for (long subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = getSubtask(subtaskId);

            if (null != subtask) {
                subtasks.add(subtask);
            }
        }

        return subtasks;
    }

    private TaskStatus calculateEpicStatus(Epic epic) {
        TaskStatus epicStatus = TaskStatus.NEW;
        final int subtasksCount = epic.getSubtaskIds().size();
        if (0 == subtasksCount) {
            return epicStatus;
        }

        int newSubtasksCount = 0;
        int doneSubtasksCount = 0;
        for (long subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = getSubtask(subtaskId);

            if (TaskStatus.NEW == subtask.getStatus()) {
                newSubtasksCount++;
            } else if (TaskStatus.DONE == subtask.getStatus()) {
                doneSubtasksCount++;
            }
        }

        if (subtasksCount == doneSubtasksCount) {
            epicStatus = TaskStatus.DONE;
        } else if (subtasksCount != newSubtasksCount) {
            epicStatus = TaskStatus.IN_PROGRESS;
        }

        return epicStatus;
    }

}
