import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = taskManager.createTask(new Task("Task 1", "Task 1 description"));
        Task task2 = taskManager.createTask(new Task("Task 2", "Task 2 description"));

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

        printAllTasks(taskManager);

        taskManager.updateTask(new Task(
                task1.getId(),
                task1.getTitle() + " updated",
                task1.getDescription() + " updated",
                TaskStatus.IN_PROGRESS
        ));
        System.out.println(taskManager.getTask(task1.getId()));

        taskManager.updateTask(new Task(
                task2.getId(),
                task2.getTitle() + " updated",
                task2.getDescription() + " updated",
                TaskStatus.DONE
        ));
        System.out.println(taskManager.getTask(task2.getId()));

        taskManager.updateEpic(new Epic(
                epic1.getId(),
                epic1.getTitle() + " updated",
                epic1.getDescription() + " updated",
                TaskStatus.IN_PROGRESS
        ));
        System.out.println(taskManager.getEpic(epic1.getId()));

        taskManager.updateEpic(new Epic(
                epic2.getId(),
                epic2.getTitle() + " updated",
                epic2.getDescription() + " updated",
                TaskStatus.DONE
        ));
        System.out.println(taskManager.getEpic(epic2.getId()));

        taskManager.updateSubtask(new Subtask(
                subtask1.getId(),
                subtask1.getTitle() + " updated",
                subtask1.getDescription() + " updated",
                TaskStatus.IN_PROGRESS,
                epic1.getId()
        ));
        System.out.println(taskManager.getSubtask(subtask1.getId()));

        taskManager.updateSubtask(new Subtask(
                subtask2.getId(),
                subtask2.getTitle() + " updated",
                subtask2.getDescription() + " updated",
                TaskStatus.DONE,
                epic1.getId()
        ));
        System.out.println(taskManager.getSubtask(subtask2.getId()));

        taskManager.updateSubtask(new Subtask(
                subtask3.getId(),
                subtask3.getTitle() + " updated",
                subtask3.getDescription() + " updated",
                TaskStatus.DONE,
                epic2.getId()
        ));
        System.out.println(taskManager.getSubtask(subtask3.getId()));

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic)) {
                System.out.println("--> " + task);
            }
        }

        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}
