package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    void testGetStartTime() {
        LocalDateTime now = LocalDateTime.now();

        Task task = new Task("Title", "Description", now, Duration.ZERO);

        assertEquals(now, task.getStartTime());
    }

    @Test
    void testGetDurationMinutes() {
        Task task = new Task("Title", "Description");
        assertNull(task.getDurationMinutes());

        task = new Task("Title", "Description", LocalDateTime.now(), Duration.ofMinutes(5));
        assertEquals(5, task.getDurationMinutes());
    }

    @Test
    void testGetEndTime() {
        LocalDateTime now = LocalDateTime.now();

        Task task = new Task(1L, "Title", "Description", TaskStatus.DONE, now, Duration.ofMinutes(30));
        assertEquals(now.plus(Duration.ofMinutes(30)), task.getEndTime());

        assertNull((new Task("Title", "Description")).getEndTime());
        assertNull((new Task("Title", "Description", now, null)).getEndTime());
    }

    @Test
    void testIntersectsWithTask() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task 1", "Task 1 description");
        Task task2 = new Task("Task 2", "Task 2 description");
        Assertions.assertFalse(task1.intersectsWithTask(task2));
        Assertions.assertFalse(task2.intersectsWithTask(task1));

        task1 = new Task("Task 1", "Task 1 description", now, Duration.ofMinutes(5));
        task2 = new Task("Task 2", "Task 2 description", now.plusMinutes(10), Duration.ofMinutes(5));
        Assertions.assertFalse(task1.intersectsWithTask(task2));
        Assertions.assertFalse(task2.intersectsWithTask(task1));

        task1 = new Task("Task 1", "Task 1 description", now, Duration.ofMinutes(5));
        task2 = new Task("Task 2", "Task 2 description", now.plusMinutes(2), Duration.ofMinutes(5));
        Assertions.assertTrue(task1.intersectsWithTask(task2));
        Assertions.assertTrue(task2.intersectsWithTask(task1));

        task1 = new Task("Task 1", "Task 1 description", now, Duration.ofMinutes(5));
        task2 = new Task("Task 2", "Task 2 description", now.minusMinutes(2), Duration.ofMinutes(5));
        Assertions.assertTrue(task1.intersectsWithTask(task2));
        Assertions.assertTrue(task2.intersectsWithTask(task1));

        task1 = new Task("Task 1", "Task 1 description", now, Duration.ofMinutes(2));
        task2 = new Task("Task 2", "Task 2 description", now.minusMinutes(2), Duration.ofMinutes(5));
        Assertions.assertTrue(task1.intersectsWithTask(task2));
        Assertions.assertTrue(task2.intersectsWithTask(task1));

        task1 = new Task("Task 1", "Task 1 description", now, Duration.ofMinutes(10));
        task2 = new Task("Task 2", "Task 2 description", now.plusMinutes(2), Duration.ofMinutes(5));
        Assertions.assertTrue(task1.intersectsWithTask(task2));
        Assertions.assertTrue(task2.intersectsWithTask(task1));

        task1 = new Task("Task 1", "Task 1 description", now.plusMinutes(10), Duration.ofMinutes(10));
        task2 = new Task("Task 2", "Task 2 description", now, Duration.ofMinutes(5));
        Assertions.assertFalse(task1.intersectsWithTask(task2));
        Assertions.assertFalse(task2.intersectsWithTask(task1));
    }

    @Test
    void testToString() {
        Task task = new Task(1L, "Task 1", "Task 1 description", TaskStatus.NEW,
                LocalDateTime.of(2025, 3, 26, 13, 30, 0), Duration.ofMinutes(5));

        Assertions.assertEquals("Task{" +
                                "id=1"+
                                ", title='Task 1'" +
                                ", description='Task 1 description'" +
                                ", status=NEW" +
                                ", startTime=2025-03-26T13:30" +
                                ", duration=PT5M" +
                                '}', task.toString());
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