import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    public static HashMap<Integer, Task> simpleTasks = new HashMap<>();
    public static HashMap<Integer, Epic> epicTasks = new HashMap<>();
    public static HashMap<Integer, SubTask> subTasks = new HashMap<>();
    public static Task task;
    public static Epic epic;
    public static SubTask subTask;

    public HashMap<Integer, Task> getSimpleTasksMap() {
        return simpleTasks;
    }

    public void addSimpleTask(Task newTask) {
        simpleTasks.put(newTask.hashCode(), newTask);
    }

    public void addEpicTask(Epic newEpic) {
        epicTasks.put(newEpic.hashCode(), newEpic);
    }

    public void addSubTaskTask(SubTask newSubTask) {
        subTasks.put(newSubTask.hashCode(), newSubTask);
    }

    public ArrayList<Task> getAllSimpleTasks () {
        return new ArrayList<>(simpleTasks.values());
    }

    public ArrayList<Epic> getAllEpicTasks () {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<SubTask> getAllSubTasks () {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllSimpleTasks() {
        simpleTasks.clear();
    }

    public void deleteAllEpicTasks() {
        epicTasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    public Task getSimpleTaskById(int id) {
        return simpleTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epicTasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

}
