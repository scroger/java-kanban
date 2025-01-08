package service.impl;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager taskManager;
    private File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = File.createTempFile("tasks", ".csv");

        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void shouldLoadTasks() throws IOException {
        taskManager.load();

        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("""
                    id,type,name,status,description,epic
                    1,TASK,Task1,NEW,Description task1,
                    2,EPIC,Epic2,DONE,Description epic2,
                    3,SUBTASK,Sub Task2,DONE,Description sub task3,2
                    """);
        }
        taskManager.load();

        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertNotNull(taskManager.getTask(1L));
        Assertions.assertEquals("Task1", taskManager.getTask(1L).getTitle());
        Assertions.assertEquals("Description task1", taskManager.getTask(1L).getDescription());
        Assertions.assertEquals(TaskStatus.NEW, taskManager.getTask(1L).getStatus());

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
        String header = "id,type,name,status,description,epic\r\n";

        taskManager.createTask(new Task("Task1", "Description task1"));
        Assertions.assertEquals(header +
                "1,TASK,Task1,NEW,Description task1,\r\n", readFile());

        Epic epic = taskManager.createEpic(new Epic("Epic2", "Description epic2"));
        Assertions.assertEquals(header +
                "1,TASK,Task1,NEW,Description task1,\r\n" +
                "2,EPIC,Epic2,NEW,Description epic2,\r\n", readFile());

        Subtask subtask = taskManager.createSubtask(new Subtask("Sub Task3", "Description sub task3", epic.getId()));
        Assertions.assertEquals(header +
                "1,TASK,Task1,NEW,Description task1,\r\n" +
                "2,EPIC,Epic2,NEW,Description epic2,\r\n" +
                "3,SUBTASK,Sub Task3,NEW,Description sub task3,2\r\n", readFile());

        taskManager.updateSubtask(new Subtask(subtask.getId(), "Sub Task2", "Description sub task3", TaskStatus.DONE, epic.getId()));
        Assertions.assertEquals(header +
                "1,TASK,Task1,NEW,Description task1,\r\n" +
                "2,EPIC,Epic2,DONE,Description epic2,\r\n" +
                "3,SUBTASK,Sub Task2,DONE,Description sub task3,2\r\n", readFile());

        taskManager.deleteEpics();
        Assertions.assertEquals(header +
                "1,TASK,Task1,NEW,Description task1,\r\n", readFile());

        taskManager.deleteTasks();
        Assertions.assertEquals(header, readFile());

        taskManager.load();
        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
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