package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import exceptions.NotFoundException;
import http.adapters.DurationAdapter;
import http.adapters.LocalDateTimeAdapter;
import model.Epic;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpTaskServerTest {

    private final TaskManager taskManager;
    private final Gson gson;
    private final HttpTaskServer taskServer;

    public HttpTaskServerTest() throws IOException {
        taskManager = Managers.getDefault();
        gson = (new GsonBuilder())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskServer = new HttpTaskServer(taskManager, gson);
    }

    @BeforeEach
    void beforeEach() {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
    }

    @Test
    void testCreateSuccess() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 2, 22, 0);

        HttpResponse<Void> createTaskResponse = createTask(new Task("Task 1", "Task 1 description", startTime, Duration.ofMinutes(5)));
        assertEquals(201, createTaskResponse.statusCode());
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");
        assertEquals("Task 1", taskManager.getTasks().get(0).getTitle(), "Некорректное имя задачи");

        HttpResponse<Void> createEpicResponse = createEpic(new Epic("Epic 1", "Epic 1 description"));
        assertEquals(201, createEpicResponse.statusCode());
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", taskManager.getEpics().get(0).getTitle(), "Некорректное имя эпика");

        HttpResponse<Void> createSubtaskResponse = createSubtask(new Subtask("Subtask 1", "Subtask 1 description", 2L, startTime.plusMinutes(6), Duration.ofMinutes(5)));
        assertEquals(201, createSubtaskResponse.statusCode());
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
        assertEquals("Subtask 1", taskManager.getSubtasks().get(0).getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    void testCreateError() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 2, 22, 0);

        HttpResponse<Void> createTaskResponse = createTask(new Task("Task 1", "Task 1 description", startTime, Duration.ofMinutes(5)));
        assertEquals(201, createTaskResponse.statusCode());
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");
        assertEquals("Task 1", taskManager.getTasks().get(0).getTitle(), "Некорректное имя задачи");

        assertEquals(406, createTask(new Task("Task 2", "Task 2 description", startTime, Duration.ofMinutes(5))).statusCode());
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");

        HttpResponse<Void> createEpicResponse = createEpic(new Epic("Epic 1", "Epic 1 description"));
        assertEquals(201, createEpicResponse.statusCode());
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", taskManager.getEpics().get(0).getTitle(), "Некорректное имя эпика");

        assertEquals(404, createSubtask(new Subtask("Subtask 1", "Subtask 1 description", null)).statusCode());
        assertEquals(404, createSubtask(new Subtask("Subtask 1", "Subtask 1 description", 5L)).statusCode());
        assertEquals(406, createSubtask(new Subtask("Subtask 2", "Subtask 2 description", 2L, startTime, Duration.ofMinutes(5))).statusCode());
        assertEquals(0, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
    }

    @Test
    void testGetAll() throws IOException, InterruptedException {
        HttpResponse<String> getTasksResponse = getRequest(
                "/tasks",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(200, getTasksResponse.statusCode());
        List<Task> tasks = gson.fromJson(getTasksResponse.body(), new TypeToken<>() {});
        assertEquals(0, tasks.size());
        assertEquals(0, taskManager.getTasks().size(), "Некорректное количество задач");

        HttpResponse<String> getEpicsResponse = getRequest(
                "/epics",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(200, getEpicsResponse.statusCode());
        List<Epic> epics = gson.fromJson(getEpicsResponse.body(), new TypeToken<>() {});
        assertEquals(0, epics.size());
        assertEquals(0, taskManager.getEpics().size(), "Некорректное количество эпиков");

        HttpResponse<String> getSubtasksResponse = getRequest(
                "/subtasks",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(200, getSubtasksResponse.statusCode());
        List<Subtask> subtasks = gson.fromJson(getSubtasksResponse.body(), new TypeToken<>() {});
        assertEquals(0, subtasks.size());
        assertEquals(0, taskManager.getSubtasks().size(), "Некорректное количество подзадач");

        assertEquals(201, createTask(new Task("Task 1", "Task 1 description")).statusCode());
        getTasksResponse = getRequest("/tasks", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, getTasksResponse.statusCode());
        tasks = gson.fromJson(getTasksResponse.body(), new TypeToken<>() {});
        assertEquals(1, tasks.size());
        assertEquals(1, taskManager.getTasks().size(), "Некорректное количество задач");

        assertEquals(201, createEpic(new Epic("Epic 1", "Epic 1 description")).statusCode());
        getEpicsResponse = getRequest("/epics", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, getEpicsResponse.statusCode());
        epics = gson.fromJson(getEpicsResponse.body(), new TypeToken<>() {});
        assertEquals(1, epics.size());
        assertEquals(1, taskManager.getEpics().size(), "Некорректное количество эпиков");

        assertEquals(201, createSubtask(new Subtask("Subtask 1", "Subtask 1 description", 2L)).statusCode());
        getSubtasksResponse = getRequest("/subtasks", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, getSubtasksResponse.statusCode());
        subtasks = gson.fromJson(getSubtasksResponse.body(), new TypeToken<>() {});
        assertEquals(1, subtasks.size());
        assertEquals(1, taskManager.getSubtasks().size(), "Некорректное количество подзадач");
    }

    @Test
    void testGetByIdSuccess() throws IOException, InterruptedException {
        assertEquals(201, createTask(new Task("Task 1", "Task 1 description")).statusCode());
        HttpResponse<String> getTaskResponse = getRequest(
                "/tasks/1",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(200, getTaskResponse.statusCode());
        Task task = gson.fromJson(getTaskResponse.body(), Task.class);
        assertEquals("Task 1", task.getTitle());
        assertEquals(taskManager.getTask(1L).getTitle(), task.getTitle());

        assertEquals(201, createEpic(new Epic("Epic 1", "Epic 1 description")).statusCode());
        HttpResponse<String> getEpicResponse = getRequest(
                "/epics/2",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(200, getEpicResponse.statusCode());
        Epic epic = gson.fromJson(getEpicResponse.body(), Epic.class);
        assertEquals("Epic 1", epic.getTitle());
        assertEquals(taskManager.getEpic(2L).getTitle(), epic.getTitle());

        assertEquals(201, createSubtask(new Subtask("Subtask 1", "Subtask 1 description", 2L)).statusCode());
        HttpResponse<String> getSubtaskResponse = getRequest(
                "/subtasks/3",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(200, getSubtaskResponse.statusCode());
        Subtask subtask = gson.fromJson(getSubtaskResponse.body(), Subtask.class);
        assertEquals("Subtask 1", subtask.getTitle());
        assertEquals(taskManager.getSubtask(3L).getTitle(), subtask.getTitle());
    }

    @Test
    void testGetByIdError() throws IOException, InterruptedException {
        HttpResponse<String> getTaskResponse = getRequest(
                "/tasks/asd",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(400, getTaskResponse.statusCode());

        getTaskResponse = getRequest(
                "/tasks/1",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(404, getTaskResponse.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.getTask(1L));

        HttpResponse<String> getEpicResponse = getRequest(
                "/epics/1",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(404, getEpicResponse.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.getEpic(1L));

        HttpResponse<String> getSubtaskResponse = getRequest(
                "/subtasks/1",
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        assertEquals(404, getSubtaskResponse.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.getSubtask(1L));
    }

    @Test
    void testDeleteByIdSuccess() throws IOException, InterruptedException {
        assertEquals(201, createTask(new Task("Task 1", "Task 1 description")).statusCode());
        assertEquals(1, taskManager.getTasks().size());

        assertEquals(201, createEpic(new Epic("Epic 1", "Epic 1 description")).statusCode());
        assertEquals(1, taskManager.getEpics().size());

        assertEquals(201, createSubtask(new Subtask("Subtask 1", "Subtask 1 description", 2L)).statusCode());
        assertEquals(1, taskManager.getSubtasks().size());

        HttpResponse<Void> deleteTaskResponse = deleteRequest("/tasks/1");
        assertEquals(201, deleteTaskResponse.statusCode());
        assertEquals(0, taskManager.getTasks().size());

        HttpResponse<Void> deleteSubtaskResponse = deleteRequest("/subtasks/3");
        assertEquals(201, deleteSubtaskResponse.statusCode());
        assertEquals(0, taskManager.getSubtasks().size());

        HttpResponse<Void> deleteEpicResponse = deleteRequest("/epics/2");
        assertEquals(201, deleteEpicResponse.statusCode());
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void testDeleteByIdError() throws IOException, InterruptedException {
        HttpResponse<Void> deleteTaskResponse = deleteRequest("/tasks/1");
        assertEquals(404, deleteTaskResponse.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.deleteTask(1L));

        HttpResponse<Void> deleteEpicResponse = deleteRequest("/epics/1");
        assertEquals(404, deleteEpicResponse.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.deleteEpic(1L));

        HttpResponse<Void> deleteSubtaskResponse = deleteRequest("/subtasks/1");
        assertEquals(404, deleteSubtaskResponse.statusCode());
        assertThrows(NotFoundException.class, () -> taskManager.deleteSubtask(1L));
    }

    @Test
    void testHistory() throws IOException, InterruptedException {
        HttpResponse<String> historyResponse = getRequest("/history", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, historyResponse.statusCode());
        List<Task> history = gson.fromJson(historyResponse.body(), new TypeToken<>() {});
        assertNotNull(history);
        assertEquals(0, history.size());

        assertEquals(201, createTask(new Task("Task 1", "Task 1 description")).statusCode());
        assertEquals(201, createEpic(new Epic("Epic 1", "Epic 1 description")).statusCode());
        assertEquals(201, createSubtask(new Subtask("Subtask 1", "Subtask 1 description", 2L)).statusCode());
        assertEquals(200, getRequest("/tasks/1", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).statusCode());
        assertEquals(200, getRequest("/epics/2", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).statusCode());
        assertEquals(200, getRequest("/subtasks/3", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).statusCode());

        historyResponse = getRequest("/history", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, historyResponse.statusCode());
        history = gson.fromJson(historyResponse.body(), new TypeToken<>() {});
        assertNotNull(history);
        assertEquals(3, history.size());
    }

    @Test
    void testPrioritized() throws IOException, InterruptedException {
        HttpResponse<String> prioritizedResponse = getRequest("/prioritized", HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, prioritizedResponse.statusCode());
        List<Task> prioritized = gson.fromJson(prioritizedResponse.body(), new TypeToken<>() {});
        assertNotNull(prioritized);
        assertEquals(0, prioritized.size());

        assertEquals(201, createTask(new Task("Task 1", "Task 1 description", LocalDateTime.of(2025, 4, 2, 23, 25), Duration.ofMinutes(500))).statusCode());
        assertEquals(406, createTask(new Task("Task 2", "Task 2 description", LocalDateTime.of(2025, 4, 2, 23, 50), Duration.ofMinutes(500))).statusCode());
        assertEquals(201, createTask(new Task("Task 2", "Task 2 description", LocalDateTime.of(2025, 4, 4, 12, 0), Duration.ofMinutes(120))).statusCode());
        assertEquals(201, createEpic(new Epic("Epic 1", "Epic 1 description")).statusCode());
        assertEquals(201, createSubtask(new Subtask("Subtask 1", "Subtask 1 description", 3L, LocalDateTime.of(2025, 4, 4, 15, 0), Duration.ofMinutes(120))).statusCode());
    }

    private HttpResponse<Void> createTask(Task task) throws IOException, InterruptedException {
        return postRequest(
                "/tasks",
                HttpRequest.BodyPublishers.ofString(gson.toJson(task)),
                HttpResponse.BodyHandlers.discarding()
        );
    }

    private HttpResponse<Void> createEpic(Epic epic) throws IOException, InterruptedException {
        return postRequest(
                "/epics",
                HttpRequest.BodyPublishers.ofString(gson.toJson(epic)),
                HttpResponse.BodyHandlers.discarding()
        );
    }

    private HttpResponse<Void> createSubtask(Subtask subtask) throws IOException, InterruptedException {
        return postRequest(
                "/subtasks",
                HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)),
                HttpResponse.BodyHandlers.discarding()
        );
    }

    private <T> HttpResponse<T> getRequest(String uri, HttpResponse.BodyHandler<T> bodyHandler)
            throws InterruptedException, IOException {
        return sendRequest(uri, "GET", HttpRequest.BodyPublishers.noBody(), bodyHandler);
    }

    private <T> HttpResponse<T> postRequest(String uri, HttpRequest.BodyPublisher bodyPublisher, HttpResponse.BodyHandler<T> bodyHandler)
            throws InterruptedException, IOException {
        return sendRequest(uri, "POST", bodyPublisher, bodyHandler);
    }

    private HttpResponse<Void> deleteRequest(String uri)
            throws InterruptedException, IOException {
        return sendRequest(uri, "DELETE", HttpRequest.BodyPublishers.noBody(), HttpResponse.BodyHandlers.discarding());
    }

    private <T> HttpResponse<T> sendRequest(String uri, String method, HttpRequest.BodyPublisher bodyPublisher, HttpResponse.BodyHandler<T> bodyHandler)
            throws InterruptedException, IOException {

        // создаём HTTP-клиент и запрос
        HttpResponse<T> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080" + uri);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .method(method, bodyPublisher)
                    .build();

            // вызываем рест, отвечающий за создание задач
            response = client.send(request, bodyHandler);
        }

        return response;
    }

}