import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager task;
    private static HistoryManager historyManager;
    @BeforeEach
    void BeforeEach() {
        task = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTask() {
        Task someTask = new Task("1 Task", "Some description", TaskStatus.NEW);
        int idTask = task.addTask(someTask);

        assertNotNull(task.getTaskById(idTask - 1), "Такого задания нет!");
        assertEquals(someTask, task.getTaskById(idTask - 1), "Задания не совпадают");
        assertEquals(task.getTaskById(idTask - 1), task.getTaskById(idTask - 1), "Задания не совпадают");

        List<Task> tempArr = task.getAllTasks();

        assertNotNull(tempArr, "Список не получен!");
        assertEquals(1, tempArr.size(), "Список пуст!");
        assertEquals(someTask, tempArr.get(0));
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
        assertEquals(someEpic, tempArr.get(0));
    }

    @Test
    void addSubTask() {
        Epic someEpic = new Epic("1 Task", "Some description");
        task.addEpicTask(someEpic);

        SubTask someSubTask = new SubTask("1 Task", "Some description", TaskStatus.NEW);
        int idTask = task.addSubTask(someEpic, someSubTask);

        assertNotNull(task.getSubTaskById(idTask - 1), "Такого задания нет!");
        assertEquals(someSubTask, task.getSubTaskById(idTask - 1), "Задания не совпадают");
        assertEquals(task.getSubTaskById(idTask - 1), task.getSubTaskById(idTask - 1), "Задания не совпадают");

        List<SubTask> tempArr = task.getAllSubTasks();

        assertNotNull(tempArr, "Список не получен!");
        assertEquals(1, tempArr.size(), "Список пуст!");
        assertEquals(someSubTask, tempArr.get(0));
    }

    @Test
    void deleteAllTasks() {
        Task someTask1 = new Task("1 Task", "Some description", TaskStatus.NEW);
        Task someTask2 = new Task("2 Task", "Some description", TaskStatus.NEW);
        Task someTask3 = new Task("3 Task", "Some description", TaskStatus.NEW);

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

        SubTask someSubTask1 = new SubTask("1 Task", "Some description", TaskStatus.NEW);
        SubTask someSubTask2 = new SubTask("2 Task", "Some description", TaskStatus.NEW);
        SubTask someSubTask3 = new SubTask("3 Task", "Some description", TaskStatus.NEW);

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

        SubTask someSubTask1 = new SubTask("1 Task", "Some description", TaskStatus.NEW);
        SubTask someSubTask2 = new SubTask("2 Task", "Some description", TaskStatus.NEW);
        SubTask someSubTask3 = new SubTask("3 Task", "Some description", TaskStatus.NEW);

        task.addSubTask(someEpic, someSubTask1);
        task.addSubTask(someEpic, someSubTask2);
        task.addSubTask(someEpic, someSubTask3);

        assertNotNull(task.getSubTaskOfEpic(someEpic), "Список не получен!");
        assertFalse(task.getSubTaskOfEpic(someEpic).isEmpty(), "Список пуст!");
    }

    @Test
    void updateTask() {
        Task someTask = new Task("1 Task", "Some description", TaskStatus.NEW);

        String taskName = "1 Task";
        String taskDescription = "Some description";
        TaskStatus taskStatus = TaskStatus.NEW;

        int idTask = task.addTask(someTask) - 1;

        Task someUpdatedTask = new Task("1.1 Task", "Some description and MORE!!!", TaskStatus.IN_PROGRESS);

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

        SubTask someTask = new SubTask("1 Task", "Some description", TaskStatus.NEW);

        String taskName = "1 Task";
        String taskDescription = "Some description";
        TaskStatus taskStatus = TaskStatus.NEW;

        int idSubTask = task.addSubTask(someEpic, someTask) - 1;

        SubTask someUpdatedSubTask = new SubTask("1.1 Task", "Some description and MORE!!!", TaskStatus.DONE);

        task.updateSubTask(someUpdatedSubTask, idSubTask);

        SubTask updatedSubTask = task.getSubTaskById(idSubTask);

        assertNotNull(updatedSubTask);
        assertNotEquals(taskName, updatedSubTask.getTaskName());
        assertNotEquals(taskDescription, updatedSubTask.getDescription());
        assertNotEquals(taskStatus, updatedSubTask.getTaskStatus());
    }

    @Test
    void deleteTaskById() {
        Task someTask = new Task("1 Task", "Some description", TaskStatus.NEW);

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

        SubTask someSubTask = new SubTask("1 Task", "Some description", TaskStatus.NEW);

        int idSubTask = task.addSubTask(someEpic, someSubTask) - 1;

        assertFalse(task.getAllSubTasks().isEmpty(), "Элемент не добавлен");
        assertEquals(someSubTask, task.getAllSubTasks().getFirst(), "Список пуст!");

        task.deleteSubTaskById(idSubTask);

        assertTrue(task.getAllSubTasks().isEmpty(), "Элемент не удален!");
    }

    /*@Test
    void getHistory() {
        historyManager.add(new Task("1 Task", "Some description", TaskStatus.NEW));

        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История пустая!");
        assertEquals(1, history.size(), "История пустая!");

        historyManager.add(new Task("2 Task", "Some description", TaskStatus.DONE));

        history = historyManager.getHistory();

        assertNotNull(history, "История пустая!");
        assertEquals(2, history.size(), "История пустая!");
        assertEquals(new Task("2 Task", "Some description", TaskStatus.DONE), history.getLast());
    }*/

    @Test
    void shouldReturnNotNullTaskManager() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void shouldReturnNotNullHistoryManager() {
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void shouldReturnArrayHistoryFiveTasks() {
        historyManager.add(new Task("First task", "Some description", TaskStatus.NEW));
        historyManager.add(new Task("Second task", "Some description", TaskStatus.IN_PROGRESS));
        historyManager.add(new Task("Third task", "Some description", TaskStatus.IN_PROGRESS));
        historyManager.add(new Epic("First epic", "Some description"));
        historyManager.add(new SubTask("First subtask", "Some description", TaskStatus.IN_PROGRESS));

        assertNotNull(historyManager.getHistory());

        assertEquals(historyManager.getHistory().size(), 5);
    }

    @Test
    void shouldReturnChangedHistory() {
        historyManager.add(new Task("First task", "Some description", TaskStatus.NEW));
        historyManager.add(new Task("Second task", "Some description", TaskStatus.IN_PROGRESS));
        historyManager.add(new Task("Third task", "Some description", TaskStatus.IN_PROGRESS));
        historyManager.add(new Epic("First epic", "Some description"));
        historyManager.add(new SubTask("First subtask", "Some description", TaskStatus.IN_PROGRESS));

        assertNotNull(historyManager.getHistory());

        List<Task> tempArr = historyManager.getHistory();

        for (Task task : tempArr) {
            System.out.println("id: " + task.getTaskId()
                    + " | task name: " + task.getTaskName() + "\n");
        }

        System.out.println("----------------------------------------------------");

        historyManager.add(new Task("Second task", "Some description", TaskStatus.IN_PROGRESS));

        tempArr = historyManager.getHistory();

        for (Task task : tempArr) {
            System.out.println("id: " + task.getTaskId()
                    + " | task name: " + task.getTaskName() + "\n");
        }
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.add(new Task("First task", "Some description", TaskStatus.NEW));
        historyManager.add(new Task("Second task", "Some description", TaskStatus.IN_PROGRESS));
        historyManager.add(new Task("Third task", "Some description", TaskStatus.IN_PROGRESS));
        historyManager.add(new Epic("First epic", "Some description"));

        assertNotNull(historyManager.getHistory());

        historyManager.remove(new Task("Third task", "Some description", TaskStatus.IN_PROGRESS).hashCode());

        assertEquals(historyManager.getHistory().size(), 3);

        List<Task> tempArr = historyManager.getHistory();

        for (Task task : tempArr) {
            System.out.println("id: " + task.getTaskId()
                    + " | task name: " + task.getTaskName() + "\n");
        }
    }
}