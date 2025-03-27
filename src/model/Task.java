package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private Long id;

    private final String title;

    private final String description;

    private TaskStatus status = TaskStatus.NEW;

    protected LocalDateTime startTime;

    protected Duration duration;

    public static final String CSV_HEADER = String.format("%s%n", String.join(",", new String[]{
            "id",
            "type",
            "name",
            "status",
            "description",
            "epic",
            "startTime",
            "duration",
            "endTime"
    }));

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Long id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        if (null != status) this.status = status;
    }

    public Task(Long id, String title, String description, TaskStatus status, LocalDateTime startTime,
                Duration duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        if (null != status) this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public Long getDurationMinutes() {
        if (null != duration) {
            return duration.toMinutes();
        }

        return null;
    }

    public LocalDateTime getEndTime() {
        if (null == startTime || null == duration) {
            return null;
        }

        return startTime.plus(duration);
    }

    public boolean intersectsWithTask(Task task) {
        if (null == startTime || null == duration || null == task.getStartTime() || null == task.getDuration()) {
            return false;
        }

        return !(task.getEndTime().isBefore(startTime)
               || task.getStartTime().isAfter(getEndTime()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                '}';
    }

    public String toCSVString() {
        return String.format(
                "%d,%s,%s,%s,%s,%s,%s,%s,%s",
                getId(),
                getType().name(),
                getTitle(),
                getStatus().name(),
                getDescription(),
                null,
                getStartTime(),
                getDurationMinutes(),
                null
        );
    }

}
