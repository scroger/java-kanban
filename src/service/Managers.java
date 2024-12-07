package service;

import service.impl.InMemoryHistoryManager;
import service.impl.InMemoryTaskManager;

public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}