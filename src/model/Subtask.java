package model;

public final class Subtask extends Task {

    private long epicId;

    public Subtask(String title, String description) {
        super(title, description);
    }

    public Subtask(String title, String description, long epicId) {
        super(title, description);

        this.epicId = epicId;
    }

    public Subtask(long id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
    }

    public Subtask(long id, String title, String description, TaskStatus status, long epicId) {
        super(id, title, description, status);

        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
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
