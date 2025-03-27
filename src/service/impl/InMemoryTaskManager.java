package service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

public class InMemoryTaskManager implements TaskManager {

    private Long taskIdCounter = 0L;
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Epic> epics = new HashMap<>();
    private final Map<Long, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected void setTaskIdCounter(Long taskIdCounter) {
        this.taskIdCounter = taskIdCounter;
    }

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
        Optional<Task> optionalTask = Optional.ofNullable(tasks.get(id));

        optionalTask.ifPresentOrElse(
                historyManager::add,
                () -> System.out.println("Task with id=" + id + " not found")
        );

        return optionalTask.orElse(null);
    }

    @Override
    public Epic getEpic(Long id) {
        Optional<Epic> optionalEpic = Optional.ofNullable(epics.get(id));

        optionalEpic.ifPresentOrElse(
                historyManager::add,
                () -> System.out.println("Epic with id=" + id + " not found")
        );

        return optionalEpic.orElse(null);
    }

    @Override
    public Subtask getSubtask(Long id) {
        Optional<Subtask> optionalSubtask = Optional.ofNullable(subtasks.get(id));

        optionalSubtask.ifPresentOrElse(
                historyManager::add,
                () -> System.out.println("Subtask with id=" + id + " not found")
        );

        return optionalSubtask.orElse(null);
    }

    @Override
    public Task createTask(Task task) {
        if (intersectsTasks(task)) {
            return null;
        }

        task.setId(generateId());

        if (TaskStatus.NEW != task.getStatus()) {
            task.setStatus(TaskStatus.NEW);
        }

        return internalCreateTask(task);
    }

    protected Task internalCreateTask(Task task) {
        tasks.put(task.getId(), task);
        if (null != task.getStartTime() && null != task.getDuration()) {
            prioritizedTasks.add(task);
        }

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());

        if (TaskStatus.NEW != epic.getStatus()) {
            epic.setStatus(TaskStatus.NEW);
        }

        return internalCreateEpic(epic);
    }

    protected Epic internalCreateEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (intersectsTasks(subtask)) {
            return null;
        }

        subtask.setId(generateId());

        if (TaskStatus.NEW != subtask.getStatus()) {
            subtask.setStatus(TaskStatus.NEW);
        }

        return internalCreateSubtask(subtask);
    }

    protected Subtask internalCreateSubtask(Subtask subtask) {
        if (null == subtask.getEpicId()) {
            System.out.println("No epic specified");

            return null;
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (null == epic) {
            System.out.println("Epic with id=" + subtask.getEpicId() + " not found");

            return null;
        }

        subtasks.put(subtask.getId(), subtask);

        epic.addSubtask(subtask.getId());
        epic.setStatus(calculateEpicStatus(epic));
        updateEpicEndTime(epic);

        if (null != subtask.getStartTime() && null != subtask.getDuration()) {
            prioritizedTasks.add(subtask);
        }

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
            updateEpicEndTime(epic);
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
                updateEpicEndTime(epic);
            } else if (Objects.equals(epic.getId(), subtask.getEpicId())) {
                epic.addSubtask(subtask.getId());

                epic.setStatus(calculateEpicStatus(epic));
                updateEpicEndTime(epic);
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

        prioritizedTasks.remove(tasks.get(id));
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(Long id) {
        Epic epic = epics.get(id);

        if (null == epic) {
            System.out.println("Epic with id=" + id + " not found");

            return;
        }

        epic.getSubtaskIds().forEach(subtaskId -> {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        });

        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void deleteSubtask(Long id) {
        Subtask subtask = subtasks.get(id);
        if (null == subtask) {
            System.out.println("Subtask with id=" + id + " not found");

            return;
        }

        prioritizedTasks.remove(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (null != epic) {
            epic.deleteSubtask(id);
            epic.setStatus(calculateEpicStatus(epic));
            updateEpicEndTime(epic);
        }

        historyManager.remove(id);
        subtasks.remove(id);
    }

    @Override
    public void deleteTasks() {
        deletePrioritizedTasksByType(TaskType.TASK);
        deleteFromHistory(tasks.keySet());
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        internalDeleteSubtasks();

        deleteFromHistory(epics.keySet());
        epics.clear();
    }

    @Override
    public void deleteSubtasks() {
        internalDeleteSubtasks();
    }

    protected void internalDeleteSubtasks() {
        getEpics().forEach(epic -> {
            epic.deleteSubtasks();
            epic.setStatus(TaskStatus.NEW);
        });

        deletePrioritizedTasksByType(TaskType.SUBTASK);
        deleteFromHistory(subtasks.keySet());
        subtasks.clear();
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void deleteFromHistory(Set<Long> ids) {
        ids.forEach(historyManager::remove);
    }

    private TaskStatus calculateEpicStatus(Epic epic) {
        TaskStatus epicStatus = TaskStatus.NEW;
        final int subtasksCount = epic.getSubtaskIds().size();
        if (0 == subtasksCount) {
            return epicStatus;
        }

        long newSubtasksCount = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(subtask -> TaskStatus.NEW == subtask.getStatus())
                .count();
        long doneSubtasksCount = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(subtask -> TaskStatus.DONE == subtask.getStatus())
                .count();

        if (subtasksCount == doneSubtasksCount) {
            epicStatus = TaskStatus.DONE;
        } else if (subtasksCount != newSubtasksCount) {
            epicStatus = TaskStatus.IN_PROGRESS;
        }

        return epicStatus;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return Set.copyOf(prioritizedTasks);
    }

    private void deletePrioritizedTasksByType(TaskType taskType) {
        prioritizedTasks.stream()
                .filter(task -> task.getType().equals(taskType))
                .toList()
                .forEach(prioritizedTasks::remove);
    }

    private boolean intersectsTasks(Task task) {
        return prioritizedTasks.stream().anyMatch(task::intersectsWithTask);
    }

    private void updateEpicEndTime(Epic epic) {
        List<Subtask> epicSubtasks = epic.getSubtaskIds()
                .stream()
                .map(subtasks::get)
                .toList();

        Duration epicDuration = epicSubtasks.stream()
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        if (!Duration.ZERO.equals(epicDuration)) {
            epic.setDuration(epicDuration);
        }

        epic.setStartTime(epicSubtasks.stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null));

        epic.setEndTime(epicSubtasks.stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null));
    }
}
