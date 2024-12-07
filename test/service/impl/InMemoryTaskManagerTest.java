package service.impl;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldReturnAllTasks() {
        taskManager.createTask(new Task("Task 1", "Task 1 description"));
        taskManager.createTask(new Task("Task 2", "Task 2 description"));

        final Collection<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    void shouldReturnAllEpics() {
        taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
        taskManager.createEpic(new Epic("Epic 2", "Epic 2 description"));

        final Collection<Epic> epics = taskManager.getEpics();

        assertNotNull(epics);
        assertEquals(2, epics.size());
    }

    @Test
    void shouldReturnAllSubtasks() {
        final Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic.getId()));

        final Collection<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks);
        assertEquals(2, subtasks.size());
    }

    @Test
    void shouldNotReturnTaskThatNotExistAndShouldNotUpdateHistory() {
        assertNull(taskManager.getTask(999L));
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void shouldReturnTaskAndUpdateHistory() {
        taskManager.createTask(new Task("Task 1", "Task 1 description"));
        final Task task = taskManager.getTask(1L);

        assertNotNull(task);
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void shouldNotReturnEpicThatNotExistAndShouldNotUpdateHistory() {
        assertNull(taskManager.getEpic(999L));
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void shouldReturnEpicAndUpdateHistory() {
        taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
        final Epic epic = taskManager.getEpic(1L);

        assertNotNull(epic);
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void shouldNotReturnSubtaskThatNotExistAndShouldNotUpdateHistory() {
        assertNull(taskManager.getSubtask(999L));
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void shouldReturnSubtaskAndUpdateHistory() {
        final Epic epic = taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic.getId()));

        final Subtask subtask = taskManager.getSubtask(2L);

        assertNotNull(subtask);
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    void shouldCreateTaskAndIgnorePassedIdAndStatus() {
        final Task task = taskManager.createTask(new Task(999L, "Task 1", "Task 1 description", TaskStatus.DONE));

        assertNotNull(task);
        assertEquals(1L, task.getId());
        assertEquals("Task 1", task.getTitle());
        assertEquals("Task 1 description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(task, new Task(1L, "Task 1", "Task 1 description", TaskStatus.NEW));
    }

    @Test
    void shouldCreateEpicAndIgnorePassedIdAndStatus() {
        final Epic epic = taskManager.createEpic(new Epic(999L, "Epic 1", "Epic 1 description", TaskStatus.DONE));

        assertNotNull(epic);
        assertEquals(1L, epic.getId());
        assertEquals("Epic 1", epic.getTitle());
        assertEquals("Epic 1 description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(epic, new Epic(1L, "Epic 1", "Epic 1 description", TaskStatus.NEW));
    }

    @Test
    void shouldNotCreateSubtaskWithoutEpic() {
        assertNull(taskManager.createSubtask(new Subtask("Subtask", "Subtask description", null)));
    }

    @Test
    void shouldNotCreateSubtaskWithEpicThatNotExist() {
        assertNull(taskManager.createSubtask(new Subtask("Subtask", "Subtask description", 1L)));
    }

    @Test
    void shouldCreateSubtaskAndIgnorePassedIdAndStatus() {
        final Subtask subtask = taskManager.createSubtask(new Subtask(
                999L,
                "Subtask",
                "Subtask description",
                TaskStatus.DONE,
                taskManager.createEpic(new Epic("Epic", "Epic description")).getId()
        ));

        assertNotNull(subtask);
        assertEquals(2L, subtask.getId());
        assertEquals("Subtask", subtask.getTitle());
        assertEquals("Subtask description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(subtask, new Subtask(2L, "Subtask", "Subtask description", TaskStatus.NEW, null));
    }

    @Test
    void shouldNotUpdateTaskThatNotExist() {
        assertNull(taskManager.updateTask(new Task(999L, "Task", "Task description", TaskStatus.DONE)));
    }

    @Test
    void shouldUpdateTask() {
        Task task = taskManager.createTask(new Task("Task", "Task description"));
        task = taskManager.updateTask(new Task(task.getId(), "Task", "Task description", TaskStatus.DONE));

        assertNotNull(task);
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void shouldNotUpdateEpicThatNotExist() {
        assertNull(taskManager.updateEpic(new Epic(999L, "Epic", "Epic description", TaskStatus.DONE)));
    }

    @Test
    void shouldUpdateEpicAndIgnorePassedStatus() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        epic = taskManager.updateEpic(new Epic(epic.getId(), "Epic updated", "Epic description updated", TaskStatus.DONE));

        assertNotNull(epic);
        assertEquals("Epic updated", epic.getTitle());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldNotUpdateSubtaskThatNotExist() {
        assertNull(taskManager.updateSubtask(new Subtask(999L, "Subtask", "Subtask description", TaskStatus.DONE, null)));
    }

    @Test
    void shouldNotUpdateSubtaskWithoutEpic() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Subtask description", epic.getId()));

        assertNull(taskManager.updateSubtask(new Subtask(subtask.getId(), "Subtask", "Subtask description", TaskStatus.DONE, null)));
    }

    @Test
    void shouldNotUpdateSubtaskWithEpicThatNotExist() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Subtask description", epic.getId()));

        assertNull(taskManager.updateSubtask(new Subtask(subtask.getId(), "Subtask", "Subtask description", TaskStatus.DONE, 999L)));
    }

    @Test
    void shouldUpdateSubtaskAndUpdateEpicStatus() {
        final Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Subtask description", epic.getId()));

        assertNotNull(subtask);
        assertEquals(TaskStatus.NEW, epic.getStatus());

        subtask = taskManager.updateSubtask(new Subtask(subtask.getId(), "Subtask updated", "Subtask description updated", TaskStatus.IN_PROGRESS, epic.getId()));

        assertNotNull(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        subtask = taskManager.updateSubtask(new Subtask(subtask.getId(), "Subtask", "Subtask description", TaskStatus.DONE, epic.getId()));

        assertNotNull(subtask);
        assertEquals(TaskStatus.DONE, epic.getStatus());

        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic.getId()));
        assertNotNull(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        subtask2 = taskManager.updateSubtask(new Subtask(subtask2.getId(), "Subtask 2", "Subtask 2 description", TaskStatus.DONE, epic.getId()));
        assertNotNull(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void shouldUpdateAndMoveSubtasksAndUpdateEpicsStatuses() {
        final Epic epic1 = taskManager.createEpic(new Epic("Epic 1", "Epic 1 subtask"));
        final Epic epic2 = taskManager.createEpic(new Epic("Epic 2", "Epic 2 subtask"));

        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic1.getId()));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Subtask 3", "Subtask 3 description", epic2.getId()));

        assertEquals(2, epic1.getSubtaskIds().size());
        assertEquals(1, epic2.getSubtaskIds().size());

        taskManager.updateSubtask(new Subtask(subtask1.getId(), subtask1.getTitle(), subtask1.getDescription(), TaskStatus.IN_PROGRESS, epic1.getId()));
        taskManager.updateSubtask(new Subtask(subtask2.getId(), subtask2.getTitle(), subtask2.getDescription(), TaskStatus.DONE, epic2.getId()));
        taskManager.updateSubtask(new Subtask(subtask3.getId(), subtask3.getTitle(), subtask3.getDescription(), TaskStatus.DONE, epic2.getId()));

        assertEquals(1, epic1.getSubtaskIds().size());
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());
        assertEquals(2, epic2.getSubtaskIds().size());
        assertEquals(TaskStatus.DONE, epic2.getStatus());

        taskManager.updateSubtask(new Subtask(subtask1.getId(), subtask1.getTitle(), subtask1.getDescription(), TaskStatus.IN_PROGRESS, epic2.getId()));

        assertEquals(0, epic1.getSubtaskIds().size());
        assertEquals(TaskStatus.NEW, epic1.getStatus());
        assertEquals(3, epic2.getSubtaskIds().size());
        assertEquals(TaskStatus.IN_PROGRESS, epic2.getStatus());
    }

    @Test
    void shouldNotDeleteTaskThatNotExist() {
        taskManager.createTask(new Task("Task", "Task description"));
        taskManager.deleteTask(999L);

        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void deleteTask() {
        taskManager.createTask(new Task("Task", "Task description"));
        taskManager.deleteTask(1L);

        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void shouldNotDeleteEpicThatNotExist() {
        taskManager.createEpic(new Epic("Epic", "Epic description"));
        taskManager.deleteEpic(999L);

        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void shouldDeleteEpicWithSubtasks() {
        final Epic epic1 = taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
        final Epic epic2 = taskManager.createEpic(new Epic("Epic 2", "Epic 2 description"));
        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic1.getId()));
        taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic1.getId()));
        taskManager.createSubtask(new Subtask("Subtask 3", "Subtask 3 description", epic2.getId()));

        assertEquals(2, taskManager.getEpics().size());
        assertEquals(3, taskManager.getSubtasks().size());

        taskManager.deleteEpic(epic1.getId());

        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    void shouldNotDeleteSubtaskThatNotExist() {
        final Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        taskManager.createSubtask(new Subtask("Subtask", "Subtask description", epic.getId()));
        taskManager.deleteSubtask(999L);

        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    void shouldDeleteSubtask() {
        final Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        final Subtask subtask = taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic.getId()));

        assertEquals(2, taskManager.getSubtasks().size());
        assertEquals(2, taskManager.getEpicSubtasks(epic).size());

        taskManager.deleteSubtask(subtask.getId());

        assertEquals(1, taskManager.getSubtasks().size());
        assertEquals(1, taskManager.getEpicSubtasks(epic).size());
    }

    @Test
    void shouldDeleteAllTasks() {
        taskManager.createTask(new Task("Task 1", "Task 1 description"));
        taskManager.createTask(new Task("Task 2", "Task 2 description"));

        assertEquals(2, taskManager.getTasks().size());

        taskManager.deleteTasks();

        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void shouldDeleteAllEpicsAndSubtasks() {
        final Epic epic1 = taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
        final Epic epic2 = taskManager.createEpic(new Epic("Epic 2", "Epic 2 description"));
        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic1.getId()));
        taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic1.getId()));
        taskManager.createSubtask(new Subtask("Subtask 3", "Subtask 3 description", epic2.getId()));

        assertEquals(2, taskManager.getEpics().size());
        assertEquals(2, taskManager.getEpicSubtasks(epic1).size());
        assertEquals(1, taskManager.getEpicSubtasks(epic2).size());
        assertEquals(3, taskManager.getSubtasks().size());

        taskManager.deleteEpics();

        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getEpicSubtasks(epic1).size());
        assertEquals(0, taskManager.getEpicSubtasks(epic2).size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void shouldDeleteAllSubtasks() {
        final Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask 3", "Subtask 3 description", epic.getId()));

        assertEquals(3, taskManager.getSubtasks().size());
        assertEquals(3, epic.getSubtaskIds().size());

        taskManager.deleteSubtasks();

        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, epic.getSubtaskIds().size());
    }

    @Test
    void shouldReturnAllEpicTasks() {
        final Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask 2", "Subtask 2 description", epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask 3", "Subtask 3 description", epic.getId()));

        assertEquals(3, taskManager.getEpicSubtasks(epic).size());
    }

    @Test
    void shouldReturnHistory() {
        final Task task = taskManager.createTask(new Task("Task", "Task description"));
        final Epic epic = taskManager.createEpic(new Epic("Epic", "Epic description"));
        final Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Subtask description", epic.getId()));

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    void shouldSaveOldTaskStatesInHistory() {
        taskManager.createTask(new Task("Task", "Task description"));
        taskManager.getTask(1L);

        taskManager.updateTask(new Task(1L, "Task updated", "Task description updated", TaskStatus.DONE));
        taskManager.getTask(1L);

        Task taskBefore = taskManager.getHistory().getFirst();
        Task taskAfter = taskManager.getHistory().getLast();

        assertNotNull(taskBefore);
        assertNotNull(taskAfter);

        assertEquals(taskBefore, taskAfter);

        assertEquals(1L, taskBefore.getId());
        assertEquals("Task", taskBefore.getTitle());
        assertEquals("Task description", taskBefore.getDescription());
        assertEquals(TaskStatus.NEW, taskBefore.getStatus());

        assertEquals(1L, taskAfter.getId());
        assertEquals("Task updated", taskAfter.getTitle());
        assertEquals("Task description updated", taskAfter.getDescription());
        assertEquals(TaskStatus.DONE, taskAfter.getStatus());
    }

}