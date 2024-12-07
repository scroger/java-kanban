package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault() {
        final TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);
        assertInstanceOf(TaskManager.class, taskManager);
    }

    @Test
    void getDefaultHistory() {
        final HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);
        assertInstanceOf(HistoryManager.class, historyManager);
    }
}