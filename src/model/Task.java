package model;

import java.util.Objects;

public class Task {

    private Long id;

    private final String title;

    private final String description;

    private TaskStatus status;

    public static final String CSV_HEADER = String.format("%s%n", String.join(",", new String[]{
            "id",
            "type",
            "name",
            "status",
            "description",
            "epic"
    }));

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(Long id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
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
               "id=" + id +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", status=" + status +
               '}';
    }

    public String toCSVString() {
        return String.format(
                "%d,%s,%s,%s,%s,",
                getId(),
                getType().name(),
                getTitle(),
                getStatus().name(),
                getDescription()
        );
    }

}
