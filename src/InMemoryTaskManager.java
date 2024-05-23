import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private static int taskId = 0;
    private final List<Task> lastSeenTasks = new ArrayList<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addTask(Task newTask) {
        tasks.put(taskId++, newTask);

        return taskId;
    }

    @Override
    public int addEpicTask(Epic newEpic) {
        epicTasks.put(taskId++, newEpic);

        return taskId;
    }

    @Override
    public int addSubTask(Epic epic, SubTask newSubTask) {
        for (Integer keys : epicTasks.keySet()) {
            if (epicTasks.get(keys).equals(epic)) {
                epicTasks.get(keys).addSubTaskToEpic(newSubTask);
                newSubTask.setEpic(epicTasks.get(keys));
                subTasks.put(taskId++, newSubTask);

                return taskId;
            }
        }

        return -1;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpicTasks() {
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer epicKeys : epicTasks.keySet()) {
            epicTasks.get(epicKeys).getSubTasks().clear();
        }

        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (historyManager.getHistory().isEmpty()) {
            historyManager.add(tasks.get(id));
        } else if (historyManager.getHistory().remove(id) != null) {
            historyManager.add(tasks.get(id));
        } else {
            historyManager.add(tasks.get(id));
        }

        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (historyManager.getHistory().isEmpty()) {
            historyManager.add(epicTasks.get(id));
        } else if (historyManager.getHistory().remove(id) != null) {
            historyManager.add(epicTasks.get(id));
        } else {
            historyManager.add(epicTasks.get(id));
        }

        return epicTasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (historyManager.getHistory().isEmpty()) {
            historyManager.add(subTasks.get(id));
        } else if (historyManager.getHistory().remove(id) != null) {
            historyManager.add(subTasks.get(id));
        } else {
            historyManager.add(subTasks.get(id));
        }

        return subTasks.get(id);
    }

    @Override
    public ArrayList<SubTask> getSubTaskOfEpic(Epic epic) {
        ArrayList<SubTask> tempArr = new ArrayList<>();

        for (Integer keys : subTasks.keySet()) {
            if (subTasks.get(keys).getEpic().equals(epic)) {
                tempArr.add(subTasks.get(keys));
            }
        }

        return tempArr;
    }

    @Override
    public void updateTask(Task task, int id) {
        tasks.put(id, task);
    }

    @Override
    public void updateEpicTask(Epic epic, int id) {
        Epic oldEpic = epicTasks.get(id);

        for (Integer subTasksKeys : subTasks.keySet()) {
            if (subTasks.get(subTasksKeys).getEpic().equals(oldEpic)) {
                subTasks.get(subTasksKeys).setEpic(epic);
            }
        }

        epicTasks.put(id, epic);
    }

    @Override
    public void updateSubTask(SubTask subTask, int id) {
        subTask.setEpic(subTasks.get(id).getEpic());
        subTasks.put(id, subTask);

        checkEpicStatus(subTasks.get(id).getEpic());
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        ArrayList<Integer> tempArr = new ArrayList<>();

        for (Integer keys : subTasks.keySet()) {
            if (subTasks.get(keys).getEpic().equals(epicTasks.get(id))) {
                tempArr.add(keys);
            }
        }

        for (Integer it : tempArr) {
            subTasks.remove(it);
        }

        epicTasks.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        Epic tempEpic = subTasks.get(id).getEpic();

        for (Integer epicKeys : epicTasks.keySet()) {
            if (subTasks.get(id).getEpic().equals(epicTasks.get(epicKeys))) {
                epicTasks.get(epicKeys).getSubTasks().remove(subTasks.get(id));
                break;
            }
        }
        subTasks.remove(id);

        checkEpicStatus(tempEpic);
    }

    @Override
    public void checkEpicStatus(Epic epic) {
        ArrayList<SubTask> tempArrSubTasks = new ArrayList<>();

        for (Integer keys : subTasks.keySet()) {
            if (subTasks.get(keys).getEpic().equals(epic)) {
                tempArrSubTasks.add(subTasks.get(keys));
            }
        }

        for (int i = 0; i < tempArrSubTasks.size(); i++) {
            if (tempArrSubTasks.get(i).getTaskStatus() == TaskStatus.IN_PROGRESS) {
                for (Integer epicKeys : epicTasks.keySet()) {
                    if (epicTasks.get(epicKeys).equals(tempArrSubTasks.get(i).getEpic())) {
                        epicTasks.get(epicKeys).setTaskStatus(TaskStatus.IN_PROGRESS);
                        return;
                    }
                }

                return;
            } else if (tempArrSubTasks.get(i).getTaskStatus() == TaskStatus.DONE) {
                for (Integer epicKeys : epicTasks.keySet()) {
                    if (epicTasks.get(epicKeys).equals(tempArrSubTasks.get(i).getEpic())) {
                        epicTasks.get(epicKeys).setTaskStatus(TaskStatus.DONE);
                        break;
                    }
                }
            } else {
                for (Integer epicKeys : epicTasks.keySet()) {
                    if (epicTasks.get(epicKeys).equals(tempArrSubTasks.get(i).getEpic())) {
                        epicTasks.get(epicKeys).setTaskStatus(TaskStatus.NEW);
                        break;
                    }
                }
            }
        }
    }

    public Map<Integer, Task> getHistory() {
        return null;
    }
}
