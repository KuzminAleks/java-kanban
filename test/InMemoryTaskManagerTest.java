import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager task;
    private static HistoryManager historyManager;

    @BeforeEach
    void BeforeEach() {
        task = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

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
        Task someTask1 = new Task("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(40), LocalDateTime.now());
        Task someTask2 = new Task("2 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(80), LocalDateTime.now());
        Task someTask3 = new Task("3 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(90), LocalDateTime.now());

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

        SubTask someSubTask1 = new SubTask("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        SubTask someSubTask2 = new SubTask("2 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        SubTask someSubTask3 = new SubTask("3 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(120), LocalDateTime.now());

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

        SubTask someSubTask1 = new SubTask("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        SubTask someSubTask2 = new SubTask("2 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(70), LocalDateTime.now());
        SubTask someSubTask3 = new SubTask("3 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(100), LocalDateTime.now());

        task.addSubTask(someEpic, someSubTask1);
        task.addSubTask(someEpic, someSubTask2);
        task.addSubTask(someEpic, someSubTask3);

        assertNotNull(task.getSubTaskOfEpic(someEpic), "Список не получен!");
        assertFalse(task.getSubTaskOfEpic(someEpic).isEmpty(), "Список пуст!");
    }

    @Test
    void updateTask() {
        Task someTask = new Task("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now());

        String taskName = "1 Task";
        String taskDescription = "Some description";
        TaskStatus taskStatus = TaskStatus.NEW;

        int idTask = task.addTask(someTask) - 1;

        Task someUpdatedTask = new Task("1.1 Task", "Some description and MORE!!!", TaskStatus.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.now());

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

        SubTask someTask = new SubTask("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        String taskName = "1 Task";
        String taskDescription = "Some description";
        TaskStatus taskStatus = TaskStatus.NEW;

        int idSubTask = task.addSubTask(someEpic, someTask) - 1;

        SubTask someUpdatedSubTask = new SubTask("1.1 Task", "Some description and MORE!!!", TaskStatus.DONE, Duration.ofMinutes(10), LocalDateTime.now());

        task.updateSubTask(someUpdatedSubTask, idSubTask);

        SubTask updatedSubTask = task.getSubTaskById(idSubTask);

        assertNotNull(updatedSubTask);
        assertNotEquals(taskName, updatedSubTask.getTaskName());
        assertNotEquals(taskDescription, updatedSubTask.getDescription());
        assertNotEquals(taskStatus, updatedSubTask.getTaskStatus());
    }

    @Test
    void deleteTaskById() {
        Task someTask = new Task("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());

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

        SubTask someSubTask = new SubTask("1 Task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        int idSubTask = task.addSubTask(someEpic, someSubTask) - 1;

        assertFalse(task.getAllSubTasks().isEmpty(), "Элемент не добавлен");
        assertEquals(someSubTask, task.getAllSubTasks().getFirst(), "Список пуст!");

        task.deleteSubTaskById(idSubTask);

        assertTrue(task.getAllSubTasks().isEmpty(), "Элемент не удален!");
    }

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
        historyManager.add(new Task("First task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));
        historyManager.add(new Task("Second task", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.now()));
        historyManager.add(new Task("Third task", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(50), LocalDateTime.now()));
        historyManager.add(new Epic("First epic", "Some description"));
        historyManager.add(new SubTask("First subtask", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(60), LocalDateTime.now()));

        assertNotNull(historyManager.getHistory());

        assertEquals(historyManager.getHistory().size(), 5);
    }

    @Test
    void shouldReturnChangedHistory() {
        historyManager.add(new Task("First task", "Some description", TaskStatus.NEW, Duration.ofMinutes(120), LocalDateTime.now()));
        historyManager.add(new Task("Second task", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(90), LocalDateTime.now()));
        historyManager.add(new Task("Third task", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.now()));
        historyManager.add(new Epic("First epic", "Some description"));
        historyManager.add(new SubTask("First subtask", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(50), LocalDateTime.now()));

        assertNotNull(historyManager.getHistory());

        List<Task> tempArr = historyManager.getHistory();

        for (Task task : tempArr) {
            System.out.println("id: " + task.getTaskId()
                    + " | task name: " + task.getTaskName() + "\n");
        }

        System.out.println("----------------------------------------------------");

        historyManager.add(new Task("Second task", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(60), LocalDateTime.now()));

        tempArr = historyManager.getHistory();

        for (Task task : tempArr) {
            System.out.println("id: " + task.getTaskId()
                    + " | task name: " + task.getTaskName() + "\n");
        }
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        int firstTask = task.addTask(new Task("First task", "Some description", TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.now()));
        int secondTask = task.addTask(new Task("Second task", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(40), LocalDateTime.now()));
        int thirdTask = task.addTask(new Task("Third task", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(120), LocalDateTime.now()));
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
    void shouldRemoveTwoTasks() {
        int firstTask = task.addTask(new Task("First task", "Some description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now()));
        int thirdTask = task.addTask(new Task("Third task", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now()));
        int firstEpic = task.addEpicTask(new Epic("First epic", "Some description"));
        int firstSubTask = task.addSubTask(new Epic("First epic", "Some description"), new SubTask("First subtask", "Some description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now()));

        task.getEpicById(firstEpic - 1);
        task.getTaskById(firstTask - 1);
        task.getTaskById(thirdTask - 1);
        task.getSubTaskById(firstSubTask - 1);

        assertNotNull(task.getHistory());
        assertEquals(task.getHistory().size(), 4);

        task.deleteSubTaskById(firstSubTask - 1);
        task.deleteTaskById(firstTask - 1);

        assertEquals(task.getHistory().size(), 2);

        List<Task> tempArr = task.getHistory();

        for (Task task : tempArr) {
            System.out.println("id: " + task.getTaskId()
                    + " | task name: " + task.getTaskName() + "\n");
        }
    }

    @Test
    void shouldWorkWithEmptyFile() {
        try {
            File file = File.createTempFile("temp1", ".txt");
            task = FileBackedTaskManager.loadFromFile(file);

            assertEquals(task.getAllTasks().size(), 0);
            assertEquals(task.getAllEpicTasks().size(), 0);
            assertEquals(task.getAllSubTasks().size(), 0);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    @Test
    void shouldSaveNineTasksInFile() {
        try {
            File file = File.createTempFile("temp2", ".txt");
            task = FileBackedTaskManager.loadFromFile(file);

            task.addTask(new Task("Task 1", "Description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.now()));

            task.addEpicTask(new Epic("Epic 1", "Description epic 1"));
            task.addSubTask(new Epic("Epic 1", "Description epic 1"), new SubTask("SubTask 1 of epic 1", "Description", TaskStatus.NEW, Duration.ofMinutes(12), LocalDateTime.of(2024,1,1,13,0)));
            task.addSubTask(new Epic("Epic 1", "Description epic 1"), new SubTask("SubTask 2 of epic 1", "Description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(65), LocalDateTime.of(2024,6,13,15,0)));
            task.addSubTask(new Epic("Epic 1", "Description epic 1"), new SubTask("SubTask 3 of epic 1", "Description", TaskStatus.DONE, Duration.ofMinutes(129), LocalDateTime.now()));

            task.addEpicTask(new Epic("Epic 2", "Description epic 2"));
            task.addSubTask(new Epic("Epic 2", "Description epic 2"), new SubTask("SubTask 1 of epic 2", "Description", TaskStatus.DONE, Duration.ofMinutes(20), LocalDateTime.now()));
            task.addSubTask(new Epic("Epic 2", "Description epic 2"), new SubTask("SubTask 2 of epic 2", "Description", TaskStatus.DONE, Duration.ofMinutes(230), LocalDateTime.now()));

            task.addTask(new Task("Task 2", "Description", TaskStatus.DONE, Duration.ofMinutes(35), LocalDateTime.now()));

            assertEquals(task.getAllTasks().size(), 2);
            assertEquals(task.getAllEpicTasks().size(), 2);
            assertEquals(task.getAllSubTasks().size(), 5);

            assertTrue(file.length() > 0);

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while (br.ready()) {
                System.out.println(br.readLine());
            }

            br.close();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    @Test
    void shouldLoadFourTasksFromFile() {
        try {
            File file = File.createTempFile("temp3", ".txt");

            FileWriter fw = new FileWriter(file);

            fw.write("id,type,name,status,description,duration,date,epic\n");
            fw.write("0,TASK,Task 1,IN_PROGRESS,Description,65,2024-12-23T15:15:30,\n");
            fw.write("1,TASK,Task 2,DONE,Description,120,2024-11-12T11:15:30,\n");
            fw.write("2,EPIC,Epic 1,IN_PROGRESS,Description epic 1,78,2024-08-22T15:15:30,\n");
            fw.write("3,SUBTASK,SubTask 1 of epic 1,IN_PROGRESS,Description,78,2024-08-22T15:15:30,2\n");
            fw.write("4,SUBTASK,SubTask 2 of epic 1,NEW,Description,90,2024-07-22T10:20:00,2\n");

            fw.close();

            task = FileBackedTaskManager.loadFromFile(file);

            List<Epic> tempArr = task.getAllEpicTasks();

            for (Epic epic : tempArr) {
                System.out.println(epic.getStartTime());
                System.out.println(epic.getEndTime());
            }

            List<Task> tempArrTasks = task.getAllTasks();

            for (Task task : tempArrTasks) {
                System.out.println(task.getTaskName() + " duration: " + task.getDuration().toHours() + "h "
                        + task.getDuration().toMinutesPart() + "min");
            }



            assertEquals(task.getAllTasks().size(), 2);
            assertEquals(task.getAllEpicTasks().size(), 1);
            assertEquals(task.getAllSubTasks().size(), 2);

            FileBackedTaskManager fbm = FileBackedTaskManager.loadFromFile(file);

            Set<Task> tempSetTask = fbm.getPrioritizedTasks();

            for (Task task : tempSetTask) {
                System.out.println(task.getTaskName() + " - " + task.getStartTime());
            }
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }
}