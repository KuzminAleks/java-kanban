package managers.interfaces;

import tasks.epic.Epic;
import tasks.subtask.SubTask;
import tasks.task.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    int addTask(Task newTask);

    int addEpicTask(Epic newEpic);

    int addSubTask(Epic epic, SubTask newSubTask);

    List<Task> getAllTasks();

    List<Epic> getAllEpicTasks();

    List<SubTask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    List<SubTask> getSubTaskOfEpic(Epic epic);

    boolean updateTask(Task task, int id);

    boolean updateEpicTask(Epic epic, int id);

    boolean updateSubTask(SubTask subTask, int id);

    boolean deleteTaskById(int id);

    boolean deleteEpicById(int id);

    boolean deleteSubTaskById(int id);

    void checkEpicStatus(Epic epic);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    boolean isIntersect(Task task);
}
