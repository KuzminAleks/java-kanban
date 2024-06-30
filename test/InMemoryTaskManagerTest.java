import managers.interfaces.HistoryManager;
import managers.inmemory.InMemoryTaskManager;
import managers.inmemory.Managers;
import tasks.epic.Epic;
import tasks.subtask.SubTask;
import tasks.task.Task;
import task.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private static HistoryManager historyManager;

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    void BeforeEach() {
        super.BeforeEach();
        historyManager = Managers.getDefaultHistory();
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
}