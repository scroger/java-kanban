package model;

import java.time.LocalDateTime;

public final class Subtask extends Task {

    private final Long epicId;

    public Subtask(String title, String description, Long epicId) {
        super(title, description);

        this.epicId = epicId;
    }

    public Subtask(String title, String description, Long epicId, LocalDateTime startTime, long durationMin) {
        super(title, description, startTime, durationMin);

        this.epicId = epicId;
    }

    public Subtask(Long id, String title, String description, TaskStatus status, Long epicId) {
        super(id, title, description, status);

        this.epicId = epicId;
    }

    public Subtask(Long id, String title, String description, TaskStatus status, Long epicId, LocalDateTime startTime,
                   long durationMin) {
        super(id, title, description, status, startTime, durationMin);

        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
               "id=" + super.getId() +
               ", epicId=" + getEpicId() +
               ", title='" + super.getTitle() + '\'' +
               ", description='" + super.getDescription() + '\'' +
               ", status=" + super.getStatus() +
               ", startTime=" + super.getStartTime() +
               ", duration=" + super.getDuration() +
               "}";
    }

    @Override
    public String toCSVString() {
        return String.format(
                "%d,%s,%s,%s,%s,%d,%s,%d,%s",
                getId(),
                getType().name(),
                getTitle(),
                getStatus().name(),
                getDescription(),
                getEpicId(),
                getStartTime(),
                getDuration().toMinutes(),
                getEndTime()
        );
    }

}
