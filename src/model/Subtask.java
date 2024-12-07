package model;

public final class Subtask extends Task {

    private final Long epicId;

    public Subtask(String title, String description, Long epicId) {
        super(title, description);

        this.epicId = epicId;
    }

    public Subtask(Long id, String title, String description, TaskStatus status, Long epicId) {
        super(id, title, description, status);

        this.epicId = epicId;
    }

    public Long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
               "id=" + super.getId() +
               ", epicId=" + epicId +
               ", title='" + super.getTitle() + '\'' +
               ", description='" + super.getDescription() + '\'' +
               ", status=" + super.getStatus() +
               "}";
    }
}
