package model;

import java.util.ArrayList;
import java.util.List;

public final class Epic extends Task {

    private final List<Long> subtaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(Long id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
    }

    public List<Long> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Long> subtaskIds) {
        deleteSubtasks();

        for (Long subtaskId : subtaskIds) {
            addSubtask(subtaskId);
        }
    }

    public void deleteSubtasks() {
        subtaskIds.clear();
    }

    public void deleteSubtask(Long id) {
        subtaskIds.remove(id);
    }

    public void addSubtask(Long id) {
        subtaskIds.add(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
               "id=" + super.getId() +
               ", title='" + super.getTitle() + '\'' +
               ", description='" + super.getDescription() + '\'' +
               ", status=" + super.getStatus() +
               ", subtaskIds=" + subtaskIds +
               "}";
    }
}
