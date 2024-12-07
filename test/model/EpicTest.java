package model;

import java.util.List;

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
    void testEquals() {
        assertEquals(
                new Epic(1L, "Title", "Description", TaskStatus.NEW),
                new Epic(1L, "Title updated", "Description updated", TaskStatus.DONE)
        );
    }

}