package service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Epic;
import model.Subtask;
import model.Task;
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
    void shouldAddNoMoreThan10InstancesOfTask() {
        historyManager.add(new Task("Task 1", "Task 1 description"));
        historyManager.add(new Epic("Epic 2", "Epic 2 description"));
        historyManager.add(new Subtask("Subtask 3", "Subtask 3 description", 999L));
        historyManager.add(new Task("Task 4", "Task 4 description"));
        historyManager.add(new Epic("Epic 5", "Epic 5 description"));
        historyManager.add(new Subtask("Subtask 6", "Subtask 6 description", 999L));
        historyManager.add(new Task("Task 7", "Task 7 description"));
        historyManager.add(new Epic("Epic 8", "Epic 8 description"));
        historyManager.add(new Subtask("Subtask 9", "Subtask 9 description", 999L));

        assertEquals(9, historyManager.getHistory().size());

        historyManager.add(new Task("Task 10", "Task 10 description"));

        assertEquals(10, historyManager.getHistory().size());
        assertEquals("Task 1", historyManager.getHistory().getFirst().getTitle());

        historyManager.add(new Epic("Epic 11", "Epic 11 description"));

        assertEquals(10, historyManager.getHistory().size());
        assertEquals("Epic 2", historyManager.getHistory().getFirst().getTitle());

        historyManager.add(new Subtask("Subtask 12", "Subtask 12 description", 999L));

        assertEquals(10, historyManager.getHistory().size());
        assertEquals("Subtask 3", historyManager.getHistory().getFirst().getTitle());
    }

    @Test
    void shouldReturnHistory() {
        historyManager.add(new Task("Task 1", "Task 1 description"));
        historyManager.add(new Epic("Epic 2", "Epic 2 description"));
        historyManager.add(new Subtask("Subtask 3", "Subtask 3 description", 999L));

        assertNotNull(historyManager.getHistory());
        assertEquals(3, historyManager.getHistory().size());
    }

}