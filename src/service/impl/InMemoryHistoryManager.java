package service.impl;

import java.util.ArrayList;
import java.util.List;

import model.Task;
import service.HistoryManager;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.removeFirst();
        }

        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

}
