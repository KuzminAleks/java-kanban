import java.util.ArrayList;

public interface TaskManager {
    void addTask(Task newTask);

    void addEpicTask(Epic newEpic);

    void addSubTask(Epic epic, SubTask newSubTask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpicTasks();

    ArrayList<SubTask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    ArrayList<SubTask> getSubTaskOfEpic(Epic epic);

    void updateTask(Task task, int id);

    void updateEpicTask(Epic epic, int id);

    void updateSubTask(SubTask subTask, int id);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubTaskById(int id);

    void checkEpicStatus(Epic epic);

     ArrayList<Task> getHistory();
}
