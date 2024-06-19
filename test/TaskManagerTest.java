import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    private T task;

    @BeforeEach
    void BeforeEach() {
        task = createManager();
    }

    protected abstract T createManager();

    @Test
    void addTask() {
        Task someTask = new Task("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        int idTask = task.addTask(someTask);
        Task task1 = task.getTaskById(idTask - 1);

        assertNotNull(task1, "Такого задания нет!");
        assertEquals(someTask, task.getTaskById(idTask - 1), "Задания не совпадают");
        assertEquals(task.getTaskById(idTask - 1), task.getTaskById(idTask - 1), "Задания не совпадают");

        List<Task> tempArr = task.getAllTasks();

        assertNotNull(tempArr, "Список не получен!");
        assertEquals(1, tempArr.size(), "Список пуст!");
        assertEquals(someTask, tempArr.getFirst());
    }

    @Test
    void addEpicTask() {
        Epic someEpic = new Epic("1 Task", "Some description");
        int idEpic = task.addEpicTask(someEpic);

        assertNotNull(task.getEpicById(idEpic - 1), "Такого задания нет!");
        assertEquals(someEpic, task.getEpicById(idEpic - 1), "Задания не совпадают");
        assertEquals(task.getEpicById(idEpic - 1), task.getEpicById(idEpic - 1), "Задания не совпадают");

        List<Epic> tempArr = task.getAllEpicTasks();

        assertNotNull(tempArr, "Список не получен!");
        assertEquals(1, tempArr.size(), "Список пуст!");
        assertEquals(someEpic, tempArr.getFirst());
    }

    @Test
    void addSubTask() {
        Epic someEpic = new Epic("1 Task", "Some description");
        task.addEpicTask(someEpic);

        SubTask someSubTask = new SubTask("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(65), LocalDateTime.now());
        int idTask = task.addSubTask(someEpic, someSubTask);

        assertNotNull(task.getSubTaskById(idTask - 1), "Такого задания нет!");
        assertEquals(someSubTask, task.getSubTaskById(idTask - 1), "Задания не совпадают");
        assertEquals(task.getSubTaskById(idTask - 1), task.getSubTaskById(idTask - 1), "Задания не совпадают");

        List<SubTask> tempArr = task.getAllSubTasks();

        assertNotNull(tempArr, "Список не получен!");
        assertEquals(1, tempArr.size(), "Список пуст!");
        assertEquals(someSubTask, tempArr.getFirst());
    }

    @Test
    void deleteAllTasks() {
        Task someTask1 = new Task("1 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(40), LocalDateTime.of(2024, 7, 13, 15, 0));
        Task someTask2 = new Task("2 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(80), LocalDateTime.of(2024, 6, 17, 10, 22));
        Task someTask3 = new Task("3 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(90), LocalDateTime.of(2024, 5, 27, 22, 45));

        task.addTask(someTask1);
        task.addTask(someTask2);
        task.addTask(someTask3);

        assertNotNull(task.getAllTasks(), "Список пуст!");

        task.deleteAllTasks();

        assertTrue(task.getAllTasks().isEmpty(), "Список не пустой!");
    }

    @Test
    void deleteAllEpicTasks() {
        Epic someEpic1 = new Epic("1 Task", "Some description");
        Epic someEpic2 = new Epic("2 Task", "Some description");
        Epic someEpic3 = new Epic("3 Task", "Some description");

        task.addEpicTask(someEpic1);
        task.addEpicTask(someEpic2);
        task.addEpicTask(someEpic3);

        assertNotNull(task.getAllEpicTasks(), "Список пуст!");

        task.deleteAllEpicTasks();

        assertTrue(task.getAllEpicTasks().isEmpty(), "Список не пустой!");
    }

    @Test
    void deleteAllSubTasks() {
        Epic someEpic = new Epic("1 Task", "Some description");

        SubTask someSubTask1 = new SubTask("1 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 11, 21, 12, 45));
        SubTask someSubTask2 = new SubTask("2 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(2024, 5, 7, 10, 25));
        SubTask someSubTask3 = new SubTask("3 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(120), LocalDateTime.of(2024, 7, 13, 16, 15));

        task.addSubTask(someEpic, someSubTask1);
        task.addSubTask(someEpic, someSubTask2);
        task.addSubTask(someEpic, someSubTask3);

        assertNotNull(task.getAllSubTasks(), "Список пуст!");

        task.deleteAllSubTasks();

        assertTrue(task.getAllSubTasks().isEmpty(), "Список не пустой!");
    }

    @Test
    void getSubTaskOfEpic() {
        Epic someEpic = new Epic("1 Task", "Some description");

        task.addEpicTask(someEpic);

        SubTask someSubTask1 = new SubTask("1 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 1, 22, 8, 45));
        SubTask someSubTask2 = new SubTask("2 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(70), LocalDateTime.of(2024, 3, 17, 17, 5));
        SubTask someSubTask3 = new SubTask("3 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(100), LocalDateTime.of(2024, 5, 27, 22, 45));

        task.addSubTask(someEpic, someSubTask1);
        task.addSubTask(someEpic, someSubTask2);
        task.addSubTask(someEpic, someSubTask3);

        assertNotNull(task.getSubTaskOfEpic(someEpic), "Список не получен!");
        assertFalse(task.getSubTaskOfEpic(someEpic).isEmpty(), "Список пуст!");
    }

    @Test
    void updateTask() {
        Task someTask = new Task("1 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2024, 2, 13, 10, 54));

        String taskName = "1 Task";
        String taskDescription = "Some description";
        TaskStatus taskStatus = TaskStatus.NEW;

        int idTask = task.addTask(someTask) - 1;

        Task someUpdatedTask = new Task("1.1 Task", "Some description and MORE!!!",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.now());

        task.updateTask(someUpdatedTask, idTask);

        Task updatedTask = task.getTaskById(idTask);

        assertNotNull(updatedTask);
        assertNotEquals(taskName, updatedTask.getTaskName());
        assertNotEquals(taskDescription, updatedTask.getDescription());
        assertNotEquals(taskStatus, updatedTask.getTaskStatus());
    }

    @Test
    void updateEpicTask() {
        Epic someEpic = new Epic("1 Epic", "Some description");

        String taskName = "1 Epic";
        String taskDescription = "Some description";

        int idEpic = task.addEpicTask(someEpic) - 1;

        Epic someUpdatedEpic = new Epic("1.1 Epic", "Some description and MORE!!!");

        task.updateEpicTask(someUpdatedEpic, idEpic);

        Epic updatedEpic = task.getEpicById(idEpic);

        assertNotNull(updatedEpic);
        assertNotEquals(taskName, updatedEpic.getTaskName());
        assertNotEquals(taskDescription, updatedEpic.getDescription());
    }

    @Test
    void updateSubTask() {
        Epic someEpic = new Epic("1 Epic", "Some description");

        task.addEpicTask(someEpic);

        SubTask someTask = new SubTask("1 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 4, 15, 18, 45));

        String taskName = "1 Task";
        String taskDescription = "Some description";
        TaskStatus taskStatus = TaskStatus.NEW;

        int idSubTask = task.addSubTask(someEpic, someTask) - 1;

        assertTrue(idSubTask > -1);

        SubTask someUpdatedSubTask = new SubTask("1.1 Task", "Some description and MORE!!!",
                TaskStatus.DONE, Duration.ofMinutes(10), LocalDateTime.now());

        task.updateSubTask(someUpdatedSubTask, idSubTask);

        SubTask updatedSubTask = task.getSubTaskById(idSubTask);

        assertNotNull(updatedSubTask);
        assertNotEquals(taskName, updatedSubTask.getTaskName());
        assertNotEquals(taskDescription, updatedSubTask.getDescription());
        assertNotEquals(taskStatus, updatedSubTask.getTaskStatus());
    }

    @Test
    void deleteTaskById() {
        Task someTask = new Task("1 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        int idTask = task.addTask(someTask) - 1;

        assertFalse(task.getAllTasks().isEmpty(), "Элемент не добавлен");
        assertEquals(someTask, task.getAllTasks().getFirst(), "Список пуст!");

        task.deleteTaskById(idTask);

        assertTrue(task.getAllTasks().isEmpty(), "Элемент не удален!");
    }

    @Test
    void deleteEpicById() {
        Epic someEpic = new Epic("1 Epic", "Some description");

        int idEpic = task.addEpicTask(someEpic) - 1;

        assertFalse(task.getAllEpicTasks().isEmpty(), "Элемент не добавлен");
        assertEquals(someEpic, task.getAllEpicTasks().getFirst(), "Список пуст!");

        task.deleteEpicById(idEpic);

        assertTrue(task.getAllEpicTasks().isEmpty(), "Элемент не удален!");
    }

    @Test
    void deleteSubTaskById() {
        Epic someEpic = new Epic("1 Epic", "Some description");

        task.addEpicTask(someEpic);

        SubTask someSubTask = new SubTask("1 Task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        int idSubTask = task.addSubTask(someEpic, someSubTask) - 1;

        assertFalse(task.getAllSubTasks().isEmpty(), "Элемент не добавлен");
        assertEquals(someSubTask, task.getAllSubTasks().getFirst(), "Список пуст!");

        task.deleteSubTaskById(idSubTask);

        assertTrue(task.getAllSubTasks().isEmpty(), "Элемент не удален!");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        int firstTask = task.addTask(new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.now()));
        int secondTask = task.addTask(new Task("Second task", "Some description",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(40), LocalDateTime.of(2024, 5, 27, 22, 45)));
        int thirdTask = task.addTask(new Task("Third task", "Some description",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(120), LocalDateTime.of(2024, 3, 12, 7, 45)));
        int firstEpic = task.addEpicTask(new Epic("First epic", "Some description"));

        task.getEpicById(firstEpic - 1);
        task.getTaskById(firstTask - 1);
        task.getTaskById(thirdTask - 1);
        task.getTaskById(secondTask - 1);

        assertNotNull(task.getHistory());
        assertEquals(task.getHistory().size(), 4);

        task.deleteTaskById(thirdTask - 1);

        assertEquals(task.getHistory().size(), 3);

        List<Task> tempArr = task.getHistory();

        for (Task task : tempArr) {
            System.out.println("id: " + task.getTaskId()
                    + " | task name: " + task.getTaskName() + "\n");
        }
    }

    @Test
    void shouldRemoveThreeTasksFromHistory() {
        int firstTask = task.addTask(new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10)));
        int thirdTask = task.addTask(new Task("Third task", "Some description",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2024, 12, 5, 13, 55)));
        int firstEpic = task.addEpicTask(new Epic("First epic", "Some description"));
        int firstSubTask = task.addSubTask(new Epic("First epic", "Some description"),
                new SubTask("First subtask", "Some description", TaskStatus.IN_PROGRESS,
                        Duration.ofMinutes(30), LocalDateTime.of(2024, 10, 3, 18, 30)));

        task.getEpicById(firstEpic - 1);
        task.getTaskById(firstTask - 1);
        task.getTaskById(thirdTask - 1);
        task.getSubTaskById(firstSubTask - 1);

        for (Task task : task.getHistory()) {
            System.out.println("id: " + task.getTaskId()
                    + " | task name: " + task.getTaskName() + "\n");
        }

        System.out.println();

        assertNotNull(task.getHistory());
        assertEquals(task.getHistory().size(), 4);

        task.deleteSubTaskById(firstSubTask - 1);
        task.deleteTaskById(firstTask - 1);
        task.deleteEpicById(firstEpic - 1);

        assertEquals(task.getHistory().size(), 1);

        List<Task> tempArr = task.getHistory();

        for (Task task : tempArr) {
            System.out.println("id: " + task.getTaskId()
                    + " | task name: " + task.getTaskName() + "\n");
        }
    }

    @Test
    void shouldReturnEpicWithNEWStatus() {
        int idEpic = task.addEpicTask(new Epic("Epic 1", "Description epic 1")) - 1;
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 1 of epic 1", "Description",
                        TaskStatus.NEW, Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0)));
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 2 of epic 1", "Description",
                        TaskStatus.NEW, Duration.ofMinutes(65), LocalDateTime.of(2024, 6, 13, 15, 0)));
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 3 of epic 1", "Description",
                        TaskStatus.NEW, Duration.ofMinutes(129), LocalDateTime.of(2024, 7, 13, 15, 0)));

        assertEquals(TaskStatus.NEW, task.getEpicById(idEpic).getTaskStatus());
    }

    @Test
    void shouldReturnEpicWithDONEStatus() {
        int idEpic = task.addEpicTask(new Epic("Epic 1", "Description epic 1")) - 1;
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 1 of epic 1", "Description",
                        TaskStatus.DONE, Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0)));
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 2 of epic 1", "Description",
                        TaskStatus.DONE, Duration.ofMinutes(65), LocalDateTime.of(2024, 6, 13, 15, 0)));
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 3 of epic 1", "Description",
                        TaskStatus.DONE, Duration.ofMinutes(129), LocalDateTime.of(2024, 7, 13, 15, 0)));

        assertEquals(TaskStatus.DONE, task.getEpicById(idEpic).getTaskStatus());
    }

    @Test
    void shouldReturnEpicWithIN_PROGRESSStatus() {
        int idEpic = task.addEpicTask(new Epic("Epic 1", "Description epic 1")) - 1;
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 1 of epic 1", "Description",
                        TaskStatus.NEW, Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0)));
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 2 of epic 1", "Description",
                        TaskStatus.DONE, Duration.ofMinutes(65), LocalDateTime.of(2024, 6, 13, 15, 0)));
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 3 of epic 1", "Description",
                        TaskStatus.NEW, Duration.ofMinutes(129), LocalDateTime.of(2024, 7, 13, 15, 0)));

        assertEquals(TaskStatus.IN_PROGRESS, task.getEpicById(idEpic).getTaskStatus());
    }

    @Test
    void shouldReturnEpicWithIN_PROGRESSStatusAlso() {
        int idEpic = task.addEpicTask(new Epic("Epic 1", "Description epic 1")) - 1;
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 1 of epic 1", "Description",
                        TaskStatus.IN_PROGRESS, Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0)));
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 2 of epic 1", "Description",
                        TaskStatus.DONE, Duration.ofMinutes(65), LocalDateTime.of(2024, 6, 13, 15, 0)));
        task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 3 of epic 1", "Description",
                        TaskStatus.NEW, Duration.ofMinutes(129), LocalDateTime.of(2024, 7, 13, 15, 0)));

        assertEquals(TaskStatus.IN_PROGRESS, task.getEpicById(idEpic).getTaskStatus());
    }

    @Test
    void shouldReturnEpicOfSubTask() {
        task.addEpicTask(new Epic("Epic 1", "Description epic 1"));

        int idSubTask = task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 1 of epic 1", "Description",
                        TaskStatus.IN_PROGRESS, Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0))) - 1;

        assertNotNull(task.getSubTaskById(idSubTask));
        assertEquals(task.getSubTaskById(idSubTask).getEpic(), new Epic("Epic 1", "Description epic 1"));
    }

    @Test
    void shouldNotAddTaskBecauseOfADate() {
        int idTask1 = task.addTask(new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10))) - 1;

        assertNotNull(task.getAllTasks());
        assertEquals(task.getAllTasks().size(), 1);
        assertEquals(task.getTaskById(idTask1), new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10)));

        task.addTask(new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10)));

        assertEquals(task.getAllTasks().size(), 1);
    }

    @Test
    void shouldNotAddSubTaskBecauseOfADate() {
        int idEpic1 = task.addEpicTask(new Epic("Epic 1", "Description epic 1")) - 1;

        assertNotNull(task.getAllEpicTasks());
        assertEquals(task.getAllEpicTasks().size(), 1);
        assertEquals(task.getEpicById(idEpic1), new Epic("Epic 1", "Description epic 1"));

        int idSubTask1 = task.addSubTask(new Epic("Epic 1", "Description epic 1"),
                new SubTask("SubTask 1 of epic 1", "Description",
                        TaskStatus.IN_PROGRESS, Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0))) - 1;

        assertNotNull(task.getAllSubTasks());
        assertEquals(task.getAllSubTasks().size(), 1);
        assertEquals(task.getSubTaskById(idSubTask1), new SubTask("SubTask 1 of epic 1", "Description",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0)));

        task.addEpicTask(new Epic("Epic 2", "Description epic 2"));

        task.addSubTask(new Epic("Epic 2", "Description epic 2"), new SubTask("SubTask 1 of epic 2", "Description",
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(12), LocalDateTime.of(2024, 1, 1, 13, 0)));

        assertEquals(task.getAllSubTasks().size(), 1);
    }

    @Test
    void shouldChangePositionTaskInHistory() {
        int idTask1 = task.addTask(new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10))) - 1;

        int idTask2 = task.addTask(new Task("Second task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 12, 22, 10, 42))) - 1;

        task.getTaskById(idTask1);
        task.getTaskById(idTask2);

        assertEquals(task.getHistory().getFirst(), new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10)));
        assertEquals(task.getHistory().getLast(), new Task("Second task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 12, 22, 10, 42)));

        for (Task task : task.getHistory()) {
            System.out.println(task.getTaskName());
        }

        System.out.println();

        task.getTaskById(idTask1);

        assertEquals(task.getHistory().getFirst(), new Task("Second task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 12, 22, 10, 42)));
        assertEquals(task.getHistory().getLast(), new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10)));

        for (Task task : task.getHistory()) {
            System.out.println(task.getTaskName());
        }
    }

    @Test
    void emptyHistory() {
        assertEquals(task.getHistory().size(), 0);
    }

    @Test
    void shouldNotAddExistedTaskInHistory() {
        int idTask1 = task.addTask(new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10))) - 1;

        task.getTaskById(idTask1);

        assertEquals(task.getHistory().size(), 1);
        assertEquals(task.getHistory().getFirst(), new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10)));

        task.getTaskById(idTask1);

        assertEquals(task.getHistory().size(), 1);
        assertEquals(task.getHistory().getFirst(), new Task("First task", "Some description",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.of(2024, 11, 2, 13, 10)));
    }
}
