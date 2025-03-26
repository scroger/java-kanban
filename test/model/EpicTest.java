package model;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testSetSubtaskIds() {
        final Epic epic = new Epic(1L, "Title", "Description", TaskStatus.NEW);
        assertEquals(0, epic.getSubtaskIds().size());

        epic.setSubtaskIds(List.of(2L, 3L, 4L));
        assertEquals(3, epic.getSubtaskIds().size());
    }

    @Test
    void testAddSubtask() {
        final Epic epic = new Epic(1L, "Title", "Description", TaskStatus.NEW);
        epic.addSubtask(2L);

        assertEquals(1, epic.getSubtaskIds().size());

        epic.addSubtask(3L);

        assertEquals(2, epic.getSubtaskIds().size());
        assertEquals(2L, epic.getSubtaskIds().getFirst());
        assertEquals(3L, epic.getSubtaskIds().getLast());
    }

    @Test
    void testDeleteSubtasks() {
        final Epic epic = new Epic(1L, "Title", "Description", TaskStatus.NEW);
        epic.setSubtaskIds(List.of(2L, 3L, 4L));

        assertEquals(3, epic.getSubtaskIds().size());

        epic.deleteSubtasks();
        assertEquals(0, epic.getSubtaskIds().size());
    }

    @Test
    void testDeleteSubtask() {
        final Epic epic = new Epic(1L, "Title", "Description", TaskStatus.NEW);
        epic.setSubtaskIds(List.of(2L, 3L, 4L));

        assertEquals(3, epic.getSubtaskIds().size());

        epic.deleteSubtask(3L);
        assertEquals(2, epic.getSubtaskIds().size());
        assertEquals(2L, epic.getSubtaskIds().getFirst());
        assertEquals(4L, epic.getSubtaskIds().getLast());
    }

    @Test
    void testToString() {
        final Epic epic = new Epic("Epic", "Epic description", LocalDateTime.of(2025, 3, 28, 12, 10));

        Assertions.assertEquals("Epic{" +
                                "id=null" +
                                ", title='Epic'" +
                                ", description='Epic description'" +
                                ", status=NEW" +
                                ", subtaskIds=[]" +
                                ", startTime=null" +
                                ", duration=null" +
                                ", endTime=2025-03-28T12:10" +
                                "}", epic.toString());
    }

    @Test
    void testEquals() {
        assertEquals(
                new Epic(1L, "Title", "Description", TaskStatus.NEW),
                new Epic(1L, "Title updated", "Description updated", TaskStatus.DONE)
        );
    }

}