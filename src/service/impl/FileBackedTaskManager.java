package service.impl;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager(File file, boolean autoload) {
        this.file = file;

        if (autoload && file.exists()) {
            load();
        }
    }

    @Override
    public Task createTask(Task task) {
        task = super.createTask(task);

        save();

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic = super.createEpic(epic);

        save();

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask = super.createSubtask(subtask);

        save();

        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        task = super.updateTask(task);

        save();

        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        epic = super.updateEpic(epic);

        save();

        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        subtask = super.updateSubtask(subtask);

        save();

        return subtask;
    }

    @Override
    public void deleteTask(Long id) {
        super.deleteTask(id);

        save();
    }

    @Override
    public void deleteEpic(Long id) {
        super.deleteEpic(id);

        save();
    }

    @Override
    public void deleteSubtask(Long id) {
        super.deleteSubtask(id);

        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();

        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();

        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();

        save();
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(String.format("%s%n", String.join(",", new String[]{
                    "id",
                    "type",
                    "name",
                    "status",
                    "description",
                    "epic"
            })));

            for (Task task : getTasks()) {
                bw.write(String.format("%s%n", task.toCSVString()));
            }

            for (Epic epic : getEpics()) {
                bw.write(String.format("%s%n", epic.toCSVString()));
            }

            for (Subtask subtask : getSubtasks()) {
                bw.write(String.format("%s%n", subtask.toCSVString()));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public void load() {
        boolean headerSkipped = false;
        long taskIdCounter = 0L;
        List<Subtask> subtasks = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String csvLine = br.readLine();

                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] taskData = csvLine.split(",");

                Long id = Long.parseLong(taskData[0]);
                if (id > taskIdCounter) {
                    taskIdCounter = id;
                }
                TaskType type = TaskType.valueOf(taskData[1]);
                String title = taskData[2];
                TaskStatus status = TaskStatus.valueOf(taskData[3]);
                String description = taskData[4];

                switch (type) {
                    case TaskType.TASK:
                        internalCreateTask(new Task(id, title, description, status));
                    break;

                    case TaskType.SUBTASK:
                        subtasks.add(new Subtask(id, title, description, status, Long.parseLong(taskData[5])));
                    break;

                    case TaskType.EPIC:
                        internalCreateEpic(new Epic(id, title, description, status));
                    break;
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }

        for (Subtask subtask : subtasks) {
            internalCreateSubtask(subtask);
        }

        setTaskIdCounter(taskIdCounter);
    }

    public static void main(String[] args) {
        File file = new File("tasks.csv");

        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        taskManager.createTask(new Task("Task 1", "Task 1 description"));
        taskManager.createTask(new Task("Task 2", "Task 2 description"));

        Epic epic1 = taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
        Epic epic2 = taskManager.createEpic(new Epic("Epic 2", "Epic 2 description"));

        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic1.getId()));
        taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic1.getId()));

        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic2.getId()));
        taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic2.getId()));

        FileBackedTaskManager taskManager2 = new FileBackedTaskManager(file, true);
        for (Task task : taskManager.getTasks()) {
            Task taskCompare = taskManager2.getTask(task.getId());

            if (null == taskCompare || !task.toCSVString().equals(taskCompare.toCSVString())) {
                System.out.println("Задачи не совпадают");
                return;
            }
        }

        for (Epic epic : taskManager.getEpics()) {
            Epic epicCompare = taskManager2.getEpic(epic.getId());

            if (null == epicCompare || !epic.toCSVString().equals(epicCompare.toCSVString())) {
                System.out.println("Эпики не совпадают");
                return;
            }
        }

        for (Subtask subtask : taskManager.getSubtasks()) {
            Subtask subtaskCompare = taskManager2.getSubtask(subtask.getId());

            if (null == subtaskCompare || !subtask.toCSVString().equals(subtaskCompare.toCSVString())) {
                System.out.println("Подзадачи не совпадают");
                return;
            }
        }

        System.out.println("Задачи, эпики и подзадачи полностью совпадают!");
    }

}
