package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testGetEpicId() {
        final Subtask subtask = new Subtask(1L, "Title", "Description", TaskStatus.NEW, 2L);

        assertEquals(2L, subtask.getEpicId());
    }

    @Test
    void testEquals() {
        assertEquals(
                new Subtask(1L, "Title", "Description", TaskStatus.NEW, 2L),
                new Subtask(1L, "Title updated", "Description updated", TaskStatus.DONE, 2L)
        );
    }

}