package service;

import java.util.List;

import model.Task;

public interface HistoryManager {

    void add(Task task);

    void remove(Long id);

    List<Task> getHistory();

}
