package model;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void testSetId() {
        final Task task = new Task("Title", "Description");
        task.setId(999L);

        assertEquals(999L, task.getId());
    }

    @Test
    void testGetId() {
        final Task task = new Task(1L, "Title", "Description", TaskStatus.NEW);

        assertEquals(1L, task.getId());
    }

    @Test
    void testGetTitle() {
        final Task task = new Task(1L, "Title", "Description", TaskStatus.NEW);

        assertEquals("Title", task.getTitle());
    }

    @Test
    void testGetDescription() {
        final Task task = new Task(1L, "Title", "Description", TaskStatus.NEW);

        assertEquals("Description", task.getDescription());
    }

    @Test
    void testGetStatus() {
        final Task task = new Task(1L, "Title", "Description", TaskStatus.NEW);

        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void testSetStatus() {
        final Task task = new Task(1L, "Title", "Description", TaskStatus.NEW);
        task.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void testEquals() {
        assertEquals(
                new Task(1L, "Title", "Description", TaskStatus.NEW),
                new Task(1L, "Title updated", "Description updated", TaskStatus.DONE)
        );
    }

    @Test
    void testHashCode() {
        final Task task = new Task(1L, "Title", "Description", TaskStatus.NEW);

        assertEquals(Objects.hash(1L), task.hashCode());
    }

}