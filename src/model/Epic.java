package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class Epic extends Task {

    private final List<Long> subtaskIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, LocalDateTime endTime) {
        super(title, description);

        this.endTime = endTime;
    }

    public Epic(Long id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
    }

    public Epic(Long id, String title, String description, TaskStatus status, LocalDateTime endTime) {
        super(id, title, description, status);

        this.endTime = endTime;
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

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
               ", subtaskIds=" + getSubtaskIds() +
               ", startTime=" + super.getStartTime() +
               ", duration=" + super.getDuration() +
               ", endTime=" + getEndTime() +
               "}";
    }

    @Override
    public String toCSVString() {
        return String.format(
                "%d,%s,%s,%s,%s,%s,%s,%s,%s",
                getId(),
                getType().name(),
                getTitle(),
                getStatus().name(),
                getDescription(),
                null,
                null,
                null,
                getEndTime()
        );
    }

}
