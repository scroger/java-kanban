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
        // Комментарий ревьюера: "Можно сразу добавить status new в объект task, перед передачи его в метод"
        // Ответ: "Это сделано для защиты от taskManager.createTask(new Task(999L, "title", "desc", TaskStatus.DONE)).
        //  Можно было бы выдавать ошибку в случае если передан id и статус, но эксепшены мы еще не проходили.
        //  Можно возвращать null в случае ошибки, но тогда придется постоянно проверять на null после вызова этого метода.
        //  Добавлю проверку статуса чтоб было понятней для чего это."
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

        subtasks.put(subtask.getId(), subtask);

        // Комментарий ревьюера: "Лучше сравнить как subtask.getEpicId()!=null"
        // Ответ: "subtask.getEpicId() был с типом long и не мог быть null.
        //  Чем Long лучше long? Согласен что 0 выглядит как магическое число.
        //  Можно было бы ввести константу NEID(not epic id) = 0 и сравнивать с ней.
        //  ОК, исправил."
        if (null != subtask.getEpicId()) {
            Epic epic = getEpic(subtask.getEpicId());

            if (null != epic) {
                epic.addSubtask(subtask.getId());
                epic.setStatus(calculateEpicStatus(epic));
            } else {
                // Комментарий ревьюера: "Если нет id эпика то лучше писать ошибку в консоль и не добавлять такую сабтаску в коллекцию"
                // Ответ: "Этого поведения не описано в задаче.
                //  Можно было бы выбрасывать исключение, но их мы еще не проходили.
                //  Можно возвращать null в случае ошибки, но придется постоянно проверять на null после вызова этого метода.
                //  Как по мне, лучше сохранить подзадачу и не привязывать её, чем выдать ошибку и потерять все что вводил пользователь."
                subtask.setEpicId(0);
            }
        }

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

        // Нельзя менять статус эпика вручную
        Epic oldEpic = getEpic(epic.getId());
        if (epic.getStatus() != oldEpic.getStatus()) {
            // Комментарий ревьюера: "После добавления сабтасок лучше проверить статус у эпика через метод calculateEpicStatus()"
            // Ответ: "Тут нет добавления сабтасок, это защита от изменения статуса эпика при его обновлении.
            //  Например: taskManager.updateEpic(new Epic(1L, "test", "test", TaskStatus.DONE))"
            epic.setStatus(oldEpic.getStatus());
        }

        // Восстанавливаем привязанные подзадачи
        epic.setSubtaskIds(oldEpic.getSubtaskIds());

        epics.put(epic.getId(), epic);

        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Subtask with id=" + subtask.getId() + " not found");

            return null;
        }

        if (null != subtask.getEpicId() && null == getEpic(subtask.getEpicId())) {
            // Комментарий ревьюера: "Аналогично - писать ошибку и не обновлять такую сабтаску"
            // Ответ: "Такого поведения не описано в задаче. ОК, исправил."
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
        // Комментарий ревьюера: "Обычно пишут сначало переменную, потом с чем сравнивать: if (epic == null)"
        // Ответ: "Это нотация Йоды во избежание опечатки (случайного присваивания вместо сравнения) сначала константа, потом переменная
        //  с древних времен когда не было нормальных IDE или они тормозили
        //  https://ru.wikipedia.org/wiki/%D0%A3%D1%81%D0%BB%D0%BE%D0%B2%D0%B8%D1%8F_%D0%99%D0%BE%D0%B4%D1%8B"
        if (null == epic) {
            System.out.println("Epic with id=" + id + " not found");

            return;
        }

        for (long subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = getSubtask(subtaskId);

            if (null != subtask) {
                // Комментарий ревьюера: "Тут следует удалить все сабтаски эпика из коллекции subtasks"
                // Ответ: "Такого поведения не описано в задаче.
                //  Добавил метод deleteEpicWithSubtasks"
                subtask.setEpicId(0);
            }
        }

        epics.remove(id);
    }

    public void deleteEpicWithSubtasks(long id) {
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
                Subtask subtask = getSubtask(subtaskId);

                if (null != subtask) {
                    // Комментарий ревьюера: "Тут следует удалить все сабтаски эпика из коллекции subtasks"
                    // Ответ: "Такого поведения не описано в задаче.
                    //  Добавил метод deleteEpicsWithSubtasks"
                    subtask.setEpicId(0);
                }
            }
        }

        epics.clear();
    }

    public void deleteEpicsWithSubtasks() {
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
