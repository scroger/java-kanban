package service.impl;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.Duration;
import java.util.List;

class FileBackedTaskManagerTest {

    @Test
    void shouldNotLoadFileThatNotExist() {
        File file = new File("testtasks.csv");
        Assertions.assertFalse(file.exists());

        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void shouldLoadEmptyFile() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void shouldLoadFileWithHeaderOnly() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(Task.CSV_HEADER);
        }
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void shouldLoadTasks() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(String.format(
                    "%s%s%n%s%n%s%n",
                    Task.CSV_HEADER,
                    "1,TASK,Task1,NEW,Description task1,null,2025-03-25T20:57:26.098771533,60,null",
                    "2,EPIC,Epic2,DONE,Description epic2,null,null,null,null",
                    "3,SUBTASK,Sub Task2,DONE,Description sub task3,2,null,null,null"
            ));
        }
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

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
    void shouldSaveTasks() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        taskManager.createTask(new Task("Task1", "Description task1"));
        Assertions.assertEquals(String.format("%s%s%n", Task.CSV_HEADER, "1,TASK,Task1,NEW,Description task1,null,null,null,null"), readFile(file));

        Epic epic = taskManager.createEpic(new Epic("Epic2", "Description epic2"));
        Assertions.assertEquals(String.format(
                "%s%s%n%s%n",
                Task.CSV_HEADER,
                "1,TASK,Task1,NEW,Description task1,null,null,null,null",
                "2,EPIC,Epic2,NEW,Description epic2,null,null,null,null"
        ), readFile(file));

        Subtask subtask = taskManager.createSubtask(new Subtask("Sub Task3", "Description sub task3", epic.getId()));
        Assertions.assertEquals(String.format(
                "%s%s%n%s%n%s%n",
                Task.CSV_HEADER,
                "1,TASK,Task1,NEW,Description task1,null,null,null,null",
                "2,EPIC,Epic2,NEW,Description epic2,null,null,null,null",
                "3,SUBTASK,Sub Task3,NEW,Description sub task3,2,null,null,null"
        ), readFile(file));

        taskManager.updateSubtask(new Subtask(subtask.getId(), "Sub Task2", "Description sub task3", TaskStatus.DONE, epic.getId()));
        Assertions.assertEquals(String.format(
                "%s%s%n%s%n%s%n",
                Task.CSV_HEADER,
                "1,TASK,Task1,NEW,Description task1,null,null,null,null",
                "2,EPIC,Epic2,DONE,Description epic2,null,null,null,null",
                "3,SUBTASK,Sub Task2,DONE,Description sub task3,2,null,null,null"
        ), readFile(file));

        taskManager.deleteEpics();
        Assertions.assertEquals(String.format(
                "%s%s%n",
                Task.CSV_HEADER,
                "1,TASK,Task1,NEW,Description task1,null,null,null,null"
        ), readFile(file));

        taskManager.deleteTasks();
        Assertions.assertEquals(Task.CSV_HEADER, readFile(file));
    }

    private String readFile(File file) throws IOException {
        StringBuilder data = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                data.append(String.format("%s%n", br.readLine()));
            }
        }

        return data.toString();
    }
}