package service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManagerTest;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void shouldNotLoadFileThatNotExist() {
        file = new File("testtasks.csv");
        Assertions.assertFalse(file.exists());

        taskManager = new FileBackedTaskManager(file);

        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void shouldLoadEmptyFile() {
        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void shouldLoadFileWithHeaderOnly() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(Task.CSV_HEADER);
        }
        taskManager = new FileBackedTaskManager(file);

        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void shouldLoadTasks() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(String.format(
                    "%s%s%n%s%n%s%n",
                    Task.CSV_HEADER,
                    "1,TASK,Task1,NEW,Description task1,null,2025-03-25T20:57:26.098771533,60,null",
                    "2,EPIC,Epic2,DONE,Description epic2,null,null,null,null",
                    "3,SUBTASK,Sub Task2,DONE,Description sub task3,2,null,null,null"
            ));
        }
        taskManager = new FileBackedTaskManager(file);

        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertNotNull(taskManager.getTask(1L));
        Assertions.assertEquals("Task1", taskManager.getTask(1L).getTitle());
        Assertions.assertEquals("Description task1", taskManager.getTask(1L).getDescription());
        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTask(1L).getStatus());
        Assertions.assertNotNull(taskManager.getTask(1L).getStartTime());
        Assertions.assertEquals(Duration.ofMinutes(60), taskManager.getTask(1L).getDuration());

        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertNotNull(taskManager.getEpic(2L));
        Assertions.assertEquals("Epic2", taskManager.getEpic(2L).getTitle());
        Assertions.assertEquals("Description epic2", taskManager.getEpic(2L).getDescription());
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpic(2L).getStatus());
        Assertions.assertArrayEquals(List.of(3L).toArray(), taskManager.getEpic(2L).getSubtaskIds().toArray());

        Assertions.assertEquals(1, taskManager.getSubtasks().size());
        Assertions.assertNotNull(taskManager.getSubtask(3L));
        Assertions.assertEquals("Sub Task2", taskManager.getSubtask(3L).getTitle());
        Assertions.assertEquals("Description sub task3", taskManager.getSubtask(3L).getDescription());
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getSubtask(3L).getStatus());
        Assertions.assertEquals(2L, taskManager.getSubtask(3L).getEpicId());
    }

    @Test
    void shouldThrowExceptionOnSaveTasks() {
        file = new File(System.getProperty("java.io.tmpdir"));
        taskManager = new FileBackedTaskManager(file);

        final Task task = new Task("Task 1", "Task 1 description");

        Assertions.assertThrows(
                ManagerSaveException.class,
                () -> taskManager.createTask(task)
        );
    }

    @Test
    void shouldSaveTasks() throws IOException {
        taskManager = new FileBackedTaskManager(file);

        taskManager.createTask(new Task("Task1", "Description task1"));
        Assertions.assertEquals(String.format("%s%s%n", Task.CSV_HEADER,
                "1,TASK,Task1,NEW,Description task1,null,null,null,null"), readFile());

        Epic epic = taskManager.createEpic(new Epic("Epic2", "Description epic2"));
        Assertions.assertEquals(String.format(
                "%s%s%n%s%n",
                Task.CSV_HEADER,
                "1,TASK,Task1,NEW,Description task1,null,null,null,null",
                "2,EPIC,Epic2,NEW,Description epic2,null,null,null,null"
        ), readFile());

        Subtask subtask = taskManager.createSubtask(new Subtask("Sub Task3", "Description sub task3", epic.getId()));
        Assertions.assertEquals(String.format(
                "%s%s%n%s%n%s%n",
                Task.CSV_HEADER,
                "1,TASK,Task1,NEW,Description task1,null,null,null,null",
                "2,EPIC,Epic2,NEW,Description epic2,null,null,null,null",
                "3,SUBTASK,Sub Task3,NEW,Description sub task3,2,null,null,null"
        ), readFile());

        taskManager.updateSubtask(new Subtask(subtask.getId(), "Sub Task2", "Description sub task3", TaskStatus.DONE,
                epic.getId()));
        Assertions.assertEquals(String.format(
                "%s%s%n%s%n%s%n",
                Task.CSV_HEADER,
                "1,TASK,Task1,NEW,Description task1,null,null,null,null",
                "2,EPIC,Epic2,DONE,Description epic2,null,null,null,null",
                "3,SUBTASK,Sub Task2,DONE,Description sub task3,2,null,null,null"
        ), readFile());

        taskManager.deleteEpics();
        Assertions.assertEquals(String.format(
                "%s%s%n",
                Task.CSV_HEADER,
                "1,TASK,Task1,NEW,Description task1,null,null,null,null"
        ), readFile());

        taskManager.deleteTasks();
        Assertions.assertEquals(Task.CSV_HEADER, readFile());
    }

    private String readFile() throws IOException {
        StringBuilder data = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                data.append(String.format("%s%n", br.readLine()));
            }
        }

        return data.toString();
    }
}