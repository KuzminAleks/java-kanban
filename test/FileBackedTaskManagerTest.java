import SaveExceptions.ManagerSaveException;
import Managers.FileBackedTaskManager;
import Managers.TaskManager;
import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import Enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static TaskManager task;

    @Override
    protected FileBackedTaskManager createManager() {
        try {
            File file = File.createTempFile("temp2", ".txt");

            return new FileBackedTaskManager(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @BeforeEach
    void BeforeEach() {
        super.BeforeEach();
    }

    @Test
    void addTask() {
        super.addTask();
    }

    @Test
    void addEpicTask() {
        super.addEpicTask();
    }

    @Test
    void addSubTask() {
        super.addSubTask();
    }

    @Test
    void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Test
    void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
    }

    @Test
    void deleteAllSubTasks() {
        super.deleteAllSubTasks();
    }

    @Test
    void getSubTaskOfEpic() {
        super.getSubTaskOfEpic();
    }

    @Test
    void updateTask() {
        super.updateTask();
    }

    @Test
    void updateEpicTask() {
        super.updateEpicTask();
    }

    @Test
    void updateSubTask() {
        super.updateSubTask();
    }

    @Test
    void deleteTaskById() {
        super.deleteTaskById();
    }

    @Test
    void deleteEpicById() {
        super.deleteEpicById();
    }

    @Test
    void deleteSubTaskById() {
        super.deleteSubTaskById();
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        super.shouldRemoveTaskFromHistory();
    }

    @Test
    void shouldRemoveThreeTasksFromHistory() {
        super.shouldRemoveThreeTasksFromHistory();
    }

    @Test
    void shouldReturnEpicWithNEWStatus() {
        super.shouldReturnEpicWithNEWStatus();
    }

    @Test
    void shouldReturnEpicWithDONEStatus() {
        super.shouldReturnEpicWithDONEStatus();
    }

    @Test
    void shouldReturnEpicWithIN_PROGRESSStatus() {
        super.shouldReturnEpicWithIN_PROGRESSStatus();
    }

    @Test
    void shouldReturnEpicWithIN_PROGRESSStatusAlso() {
        super.shouldReturnEpicWithIN_PROGRESSStatusAlso();
    }

    @Test
    void shouldReturnEpicOfSubTask() {
        super.shouldReturnEpicOfSubTask();
    }

    @Test
    void shouldNotAddTaskBecauseOfADate() {
        super.shouldNotAddTaskBecauseOfADate();
    }

    @Test
    void shouldNotAddSubTaskBecauseOfADate() {
        super.shouldNotAddSubTaskBecauseOfADate();
    }

    @Test
    void shouldChangePositionTaskInHistory() {
        super.shouldChangePositionTaskInHistory();
    }

    @Test
    void emptyHistory() {
        super.emptyHistory();
    }

    @Test
    void shouldNotAddExistedTaskInHistory() {
        super.shouldNotAddExistedTaskInHistory();
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

            task.addTask(new Task("Tasks.Task 1", "Description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.now()));

            task.addEpicTask(new Epic("Tasks.Epic 1", "Description epic 1"));
            task.addSubTask(new Epic("Tasks.Epic 1", "Description epic 1"), new SubTask("Tasks.SubTask 1 of epic 1", "Description", TaskStatus.NEW, Duration.ofMinutes(12), LocalDateTime.of(2024,1,1,13,0)));
            task.addSubTask(new Epic("Tasks.Epic 1", "Description epic 1"), new SubTask("Tasks.SubTask 2 of epic 1", "Description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(65), LocalDateTime.of(2024,6,13,15,0)));
            task.addSubTask(new Epic("Tasks.Epic 1", "Description epic 1"), new SubTask("Tasks.SubTask 3 of epic 1", "Description", TaskStatus.DONE, Duration.ofMinutes(129), LocalDateTime.of(2024,7,13,15,0)));

            task.addEpicTask(new Epic("Tasks.Epic 2", "Description epic 2"));
            task.addSubTask(new Epic("Tasks.Epic 2", "Description epic 2"), new SubTask("Tasks.SubTask 1 of epic 2", "Description", TaskStatus.DONE, Duration.ofMinutes(20), LocalDateTime.of(2024,12,13,15,0)));
            task.addSubTask(new Epic("Tasks.Epic 2", "Description epic 2"), new SubTask("Tasks.SubTask 2 of epic 2", "Description", TaskStatus.DONE, Duration.ofMinutes(230), LocalDateTime.of(2024,11,13,15,0)));

            task.addTask(new Task("Tasks.Task 2", "Description", TaskStatus.DONE, Duration.ofMinutes(35), LocalDateTime.of(2024,6,14,15,0)));

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
    void testException() {
        assertThrows(ManagerSaveException.class, () -> task = FileBackedTaskManager.loadFromFile(new File("IncorrectFile.txt")));
    }
}
