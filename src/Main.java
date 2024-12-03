import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("========== Проверяем создание задач ==========");
        Task task1 = taskManager.createTask(new Task("Task 1", "Task 1 description"));
        Task task2 = taskManager.createTask(new Task("Task 2", "Task 2 description"));
        System.out.println("Задачи: " + taskManager.getTasks());

        System.out.println();
        System.out.println("========== Проверяем создание эпиков и подзадач ==========");
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask(
                "Epic 1 subtask 1",
                "Epic 1 subtask 1 description",
                epic1.getId()
        ));
        Subtask subtask2 = taskManager.createSubtask(new Subtask(
                "Epic 1 subtask 2",
                "Epic 1 subtask 2 description",
                epic1.getId()
        ));

        Epic epic2 = taskManager.createEpic(new Epic("Epic 2", "Epic 2 description"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask(
                "Epic 2 subtask 1",
                "Epic 2 subtask 1 description",
                epic2.getId()
        ));
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        System.out.println();
        System.out.println("========== Проверяем обновление задач ==========");
        System.out.println("Задачи с id=999 не должно быть найдено");
        System.out.println();
        taskManager.updateTask(new Task(999L, "Test", "Test", TaskStatus.IN_PROGRESS));
        taskManager.updateTask(new Task(
                task1.getId(),
                task1.getTitle() + " updated",
                task1.getDescription() + " updated",
                TaskStatus.IN_PROGRESS
        ));
        Task updatedTask2 = taskManager.updateTask(new Task(
                task2.getId(),
                task2.getTitle() + " updated",
                task2.getDescription() + " updated",
                TaskStatus.DONE
        ));
        if (null != updatedTask2) {
            task2 = updatedTask2;
        }
        System.out.println("Задачи: " + taskManager.getTasks());

        System.out.println();
        System.out.println("========== Проверяем обновление эпиков ==========");
        System.out.println("Эпика с id=999 не должно быть найдено");
        System.out.println("Статус эпиков не должен измениться и остаться NEW");
        System.out.println();
        taskManager.updateEpic(new Epic(999L, "Test", "Test", TaskStatus.DONE));
        Epic updatedEpic1 = taskManager.updateEpic(new Epic(
                epic1.getId(),
                epic1.getTitle() + " updated",
                epic1.getDescription() + " updated",
                TaskStatus.IN_PROGRESS
        ));
        if (null != updatedEpic1) {
            epic1 = updatedEpic1;
        }
        Epic updatedEpic2 = taskManager.updateEpic(new Epic(
                epic2.getId(),
                epic2.getTitle() + " updated",
                epic2.getDescription() + " updated",
                TaskStatus.DONE
        ));
        if (null != updatedEpic2) {
            epic2 = updatedEpic2;
        }
        System.out.println("Эпики: " + taskManager.getEpics());

        System.out.println();
        System.out.println("========== Проверяем обновление подзадач ==========");
        System.out.println("Подзадачи с id=999 не должно быть найдено");
        System.out.println("Статус эпика с id=" + epic1.getId() + " должен стать IN_PROGRESS");
        System.out.println("Статус эпика с id=" + epic2.getId() + " должен стать DONE");
        System.out.println();
        taskManager.updateSubtask(new Subtask(999L, "Test", "Test", TaskStatus.IN_PROGRESS, epic2.getId()));
        Subtask updatedSubtask1 = taskManager.updateSubtask(new Subtask(
                subtask1.getId(),
                subtask1.getTitle() + " updated",
                subtask1.getDescription() + " updated",
                TaskStatus.IN_PROGRESS,
                epic1.getId()
        ));
        if (null != updatedSubtask1) {
            subtask1 = updatedSubtask1;
        }
        Subtask updatedSubtask2 = taskManager.updateSubtask(new Subtask(
                subtask2.getId(),
                subtask2.getTitle() + " updated",
                subtask2.getDescription() + " updated",
                TaskStatus.DONE,
                epic1.getId()
        ));
        if (null != updatedSubtask2) {
            subtask2 = updatedSubtask2;
        }
        Subtask updatedSubtask3 = taskManager.updateSubtask(new Subtask(
                subtask3.getId(),
                subtask3.getTitle() + " updated",
                subtask3.getDescription() + " updated",
                TaskStatus.DONE,
                epic2.getId()
        ));
        if (null != updatedSubtask3) {
            subtask3 = updatedSubtask3;
        }
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        System.out.println();
        System.out.println("========== Проверяем перенос подзадач ==========");
        System.out.println("Подзадача с id=" + subtask1.getId() + " должна перенестись в эпик с id=" + epic2.getId());
        System.out.println("Статус эпика с id=" + epic1.getId() + " должен стать DONE");
        System.out.println("Статус эпика с id=" + epic2.getId() + " должен стать IN_PROGRESS");
        System.out.println();
        updatedSubtask1 = taskManager.updateSubtask(new Subtask(
                subtask1.getId(),
                subtask1.getTitle(),
                subtask1.getDescription(),
                subtask1.getStatus(),
                epic2.getId()
        ));
        if (null != updatedSubtask1) {
            subtask1 = updatedSubtask1;
        }
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        System.out.println();
        System.out.println("========== Проверяем перенос эпиков ==========");
        System.out.println("Подзадача с id=" + subtask2.getId() + " не должна обновиться и должна остаться в эпике с id=" + epic1.getId());
        System.out.println("Подзадача с id=" + subtask3.getId() + " не должна обновиться и должна остаться в эпике с id=" + epic2.getId());
        System.out.println();
        updatedSubtask2 = taskManager.updateSubtask(new Subtask(
                subtask2.getId(),
                subtask2.getTitle() + " new",
                subtask2.getDescription(),
                subtask2.getStatus(),
                999L
        ));
        if (null != updatedSubtask2) {
            subtask2 = updatedSubtask2;
        }
        updatedSubtask3 = taskManager.updateSubtask(new Subtask(
                subtask3.getId(),
                subtask3.getTitle(),
                subtask3.getDescription(),
                subtask3.getStatus()
        ));
        if (null != updatedSubtask3) {
            subtask3 = updatedSubtask3;
        }
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        System.out.println();
        System.out.println("========== Проверяем удаление задачи ==========");
        taskManager.deleteTask(999L);
        taskManager.deleteTask(task2.getId());
        System.out.println("Задачи: " + taskManager.getTasks());

        System.out.println();
        System.out.println("========== Проверяем удаление эпика ==========");
        System.out.println("Подзадача с id=" + subtask2.getId() + " должна удалиться вместе с эпиком c id=" + epic1.getId());
        taskManager.deleteEpic(999L);
        taskManager.deleteEpic(epic1.getId());
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        System.out.println();
        System.out.println("========== Проверяем удаление подзадачи ==========");
        System.out.println("Эпик с id=" + epic2.getId() + " должен обновить статус на NEW так как в нем больше нет задач");
        System.out.println();
        taskManager.deleteSubtask(999L);
        taskManager.deleteSubtask(subtask1.getId());
        taskManager.deleteSubtask(subtask3.getId());
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        System.out.println();
        System.out.println("========== Возвращаем подзадачу эпику ==========");
        System.out.println("Эпик с id=" + epic2.getId() + " должен обновить статус на DONE так как в нем появится одна подзадача со статусом DONE");
        System.out.println();
        epic1 = taskManager.createEpic(new Epic("Epic 1", "Epic 1 description"));
        taskManager.createSubtask(new Subtask("Subtask 1", "Subtask 1 description", epic1.getId()));
        taskManager.createSubtask(new Subtask(
                subtask2.getId(),
                subtask2.getTitle(),
                subtask2.getDescription(),
                subtask2.getStatus(),
                epic2.getId()
        ));
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        System.out.println();
        System.out.println("========== Проверяем удаление всех задач и подзадач ==========");
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        System.out.println("Задачи: " + taskManager.getTasks());
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        System.out.println();
        System.out.println("========== Проверяем удаление всех эпиков ==========");
        taskManager.deleteEpics();
        System.out.println("Задачи: " + taskManager.getTasks());
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println("Подзадачи: " + taskManager.getSubtasks());
    }

}
