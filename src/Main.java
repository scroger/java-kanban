import model.Epic;
import model.Subtask;
import model.Task;
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
        Subtask subtask3 = taskManager.createSubtask(new Subtask(
                "Epic 2 subtask 1",
                "Epic 2 subtask 1 description",
                epic1.getId()
        ));

        taskManager.getSubtask(subtask2.getId());
        printHistory(taskManager);

        taskManager.getTask(task2.getId());
        printHistory(taskManager);

        taskManager.getSubtask(subtask1.getId());
        printHistory(taskManager);

        taskManager.getEpic(epic1.getId());
        printHistory(taskManager);

        taskManager.getTask(task2.getId());
        printHistory(taskManager);

        taskManager.getSubtask(subtask3.getId());
        printHistory(taskManager);

        taskManager.getSubtask(subtask1.getId());
        printHistory(taskManager);

        taskManager.getTask(task1.getId());
        printHistory(taskManager);

        taskManager.getSubtask(subtask2.getId());
        printHistory(taskManager);

        taskManager.getEpic(epic1.getId());
        printHistory(taskManager);

        taskManager.deleteTask(task2.getId());
        printHistory(taskManager);

        taskManager.deleteEpic(epic1.getId());
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}
