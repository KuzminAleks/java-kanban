import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    public static HashMap<Integer, Task> tasks = new HashMap<>();
    public static HashMap<Integer, Epic> epicTasks = new HashMap<>();
    public static HashMap<Integer, SubTask> subTasks = new HashMap<>();
    public static Task task;
    public static Epic epic;
    public static SubTask subTask;
    private static int taskId = 0;

    public HashMap<Integer, Task> getTasksMap() { //Убрать!!!
        return tasks;
    }

    public void addTask(Task newTask) {
        tasks.put(taskId++, newTask);
    }

    public void addEpicTask(Epic newEpic) {
        epicTasks.put(taskId++, newEpic);
    }

    public void addSubTask(Epic epic, SubTask newSubTask) {
        for (Integer keys : epicTasks.keySet()) {
            if (epicTasks.get(keys).equals(epic)) {
                epicTasks.get(keys).addSubTaskToEpic(newSubTask);
                newSubTask.setEpic(epicTasks.get(keys));
            }
        }

        subTasks.put(taskId++, newSubTask);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpicTasks() {
        epicTasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epicTasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public void updateTask(Task task, int id) {
        tasks.put(id, task);
    }

    public void updateEpicTask(Epic epic, int id) {
        epicTasks.put(id, epic);
    }

    public void updateSubTask(SubTask subTask, int id) {
        subTasks.put(id, subTask);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        ArrayList<Integer> tempArr = new ArrayList<>();
        for (Integer keys : subTasks.keySet()) {
            if (subTasks.get(keys).epic.equals(epicTasks.get(id))) {
                tempArr.add(keys);
            }
        }

        for (Integer it : tempArr) {
            subTasks.remove(it);
        }

        epicTasks.remove(id);
    }

    public void deleteSubTaskById(int id) {
        subTasks.remove(id);
    }
}
