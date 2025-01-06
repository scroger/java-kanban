package service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.HistoryManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddItemsWithoutDuplicatesToTheEnd() {
        Task task = new Task(1L, "Task", "Task description", TaskStatus.NEW);
        Epic epic = new Epic(2L, "Epic", "Epic description", TaskStatus.NEW);
        Subtask subtask = new Subtask(3L, "Subtask", "Subtask description", TaskStatus.NEW, epic.getId());

        assertEquals(0, historyManager.getHistory().size());

        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().getLast());

        historyManager.add(epic);

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(epic, historyManager.getHistory().getLast());

        historyManager.add(subtask);

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(subtask, historyManager.getHistory().getLast());

        historyManager.add(task);

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().getLast());
    }

    @Test
    void shouldRemoveFirst() {
        historyManager.add(new Task(1L, "Task", "Task description", TaskStatus.NEW));
        historyManager.add(new Epic(2L, "Epic", "Epic description", TaskStatus.NEW));
        historyManager.add(new Subtask(3L, "Subtask", "Subtask description", TaskStatus.NEW, 2L));

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(1L, historyManager.getHistory().getFirst().getId());

        historyManager.remove(1L);

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(2L, historyManager.getHistory().getFirst().getId());
    }

    @Test
    void shouldRemoveLast() {
        historyManager.add(new Task(1L, "Task", "Task description", TaskStatus.NEW));
        historyManager.add(new Epic(2L, "Epic", "Epic description", TaskStatus.NEW));
        historyManager.add(new Subtask(3L, "Subtask", "Subtask description", TaskStatus.NEW, 2L));

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(3L, historyManager.getHistory().getLast().getId());

        historyManager.remove(3L);

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(2L, historyManager.getHistory().getLast().getId());
    }

    @Test
    void shouldRemoveSecond() {
        historyManager.add(new Task(1L, "Task", "Task description", TaskStatus.NEW));
        historyManager.add(new Epic(2L, "Epic", "Epic description", TaskStatus.NEW));
        historyManager.add(new Subtask(3L, "Subtask", "Subtask description", TaskStatus.NEW, 2L));

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(1L, historyManager.getHistory().getFirst().getId());
        assertEquals(3L, historyManager.getHistory().getLast().getId());

        historyManager.remove(2L);

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(1L, historyManager.getHistory().getFirst().getId());
        assertEquals(3L, historyManager.getHistory().getLast().getId());
    }

    @Test
    void shouldAddMoreThan10InstancesOfTask() {
        historyManager.add(new Task(1L, "Task", "Task description", TaskStatus.NEW));
        historyManager.add(new Epic(2L, "Epic", "Epic description", TaskStatus.NEW));
        historyManager.add(new Subtask(3L, "Subtask", "Subtask description", TaskStatus.NEW, 2L));
        historyManager.add(new Task(4L, "Task", "Task description", TaskStatus.NEW));
        historyManager.add(new Epic(5L, "Epic", "Epic description", TaskStatus.NEW));
        historyManager.add(new Subtask(6L, "Subtask", "Subtask description", TaskStatus.NEW, 5L));
        historyManager.add(new Task(7L, "Task", "Task description", TaskStatus.NEW));
        historyManager.add(new Epic(8L, "Epic", "Epic description", TaskStatus.NEW));
        historyManager.add(new Subtask(9L, "Subtask", "Subtask description", TaskStatus.NEW, 8L));
        historyManager.add(new Task(10L, "Task", "Task description", TaskStatus.NEW));
        historyManager.add(new Epic(11L, "Epic", "Epic description", TaskStatus.NEW));

        assertEquals(11, historyManager.getHistory().size());
    }

    @Test
    void shouldReturnHistory() {
        historyManager.add(new Task(1L, "Task", "Task description", TaskStatus.NEW));
        historyManager.add(new Epic(2L, "Epic", "Epic description", TaskStatus.NEW));
        historyManager.add(new Subtask(3L, "Subtask", "Subtask description", TaskStatus.NEW, 2L));

        assertNotNull(historyManager.getHistory());
        assertEquals(3, historyManager.getHistory().size());
    }

}