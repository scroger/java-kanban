package service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

public class InMemoryTaskManager implements TaskManager {

    private Long taskIdCounter = 0L;
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Epic> epics = new HashMap<>();
    private final Map<Long, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private Long generateId() {
        return ++taskIdCounter;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(Long id) {
        Task task = tasks.get(id);
        if (null == task) {
            System.out.println("Task with id=" + id + " not found");

            return null;
        }

        historyManager.add(task);

        return task;
    }

    @Override
    public Epic getEpic(Long id) {
        Epic epic = epics.get(id);
        if (null == epic) {
            System.out.println("Epic with id=" + id + " not found");

            return null;
        }

        historyManager.add(epic);

        return epic;
    }

    @Override
    public Subtask getSubtask(Long id) {
        Subtask subtask = subtasks.get(id);
        if (null == subtask) {
            System.out.println("Subtask with id=" + id + " not found");

            return null;
        }

        historyManager.add(subtask);

        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());

        if (TaskStatus.NEW != task.getStatus()) {
            task.setStatus(TaskStatus.NEW);
        }

        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());

        if (TaskStatus.NEW != epic.getStatus()) {
            epic.setStatus(TaskStatus.NEW);
        }

        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (null == subtask.getEpicId()) {
            System.out.println("No epic specified");

            return null;
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (null == epic) {
            System.out.println("Epic with id=" + subtask.getEpicId() + " not found");

            return null;
        }

        subtask.setId(generateId());

        if (TaskStatus.NEW != subtask.getStatus()) {
            subtask.setStatus(TaskStatus.NEW);
        }

        subtasks.put(subtask.getId(), subtask);

        epic.addSubtask(subtask.getId());
        epic.setStatus(calculateEpicStatus(epic));

        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Task with id=" + task.getId() + " not found");

            return null;
        }

        tasks.put(task.getId(), task);

        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Epic with id=" + epic.getId() + " not found");

            return null;
        }

        // Восстанавливаем привязанные подзадачи
        Epic oldEpic = epics.get(epic.getId());
        epic.setSubtaskIds(oldEpic.getSubtaskIds());

        // Нельзя менять статус эпика вручную
        if (epic.getStatus() != oldEpic.getStatus()) {
            epic.setStatus(calculateEpicStatus(epic));
        }

        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Subtask with id=" + subtask.getId() + " not found");

            return null;
        }

        if (null == subtask.getEpicId()) {
            System.out.println("No epic specified");

            return null;
        }

        if (null == epics.get(subtask.getEpicId())) {
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

    @Override
    public void deleteTask(Long id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Task with id=" + id + " not found");

            return;
        }

        tasks.remove(id);
    }

    @Override
    public void deleteEpic(Long id) {
        Epic epic = epics.get(id);

        if (null == epic) {
            System.out.println("Epic with id=" + id + " not found");

            return;
        }

        for (Long subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(id);
    }

    @Override
    public void deleteSubtask(Long id) {
        Subtask subtask = subtasks.get(id);
        if (null == subtask) {
            System.out.println("Subtask with id=" + id + " not found");

            return;
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (null != epic) {
            epic.deleteSubtask(id);
            epic.setStatus(calculateEpicStatus(epic));
        }

        subtasks.remove(id);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : getEpics()) {
            for (Long subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }

        epics.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : getEpics()) {
            epic.deleteSubtasks();
            epic.setStatus(TaskStatus.NEW);
        }

        subtasks.clear();
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        List<Subtask> epicSubtasks = new ArrayList<>();

        for (Long subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);

            if (null != subtask) {
                epicSubtasks.add(subtask);
            }
        }

        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private TaskStatus calculateEpicStatus(Epic epic) {
        TaskStatus epicStatus = TaskStatus.NEW;
        final int subtasksCount = epic.getSubtaskIds().size();
        if (0 == subtasksCount) {
            return epicStatus;
        }

        int newSubtasksCount = 0;
        int doneSubtasksCount = 0;
        for (Long subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);

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
