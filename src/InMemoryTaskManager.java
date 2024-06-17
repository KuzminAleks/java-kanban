import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epicTasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    private static int taskId = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> sortedTasks = new TreeSet<>((Task task1, Task task2) -> (task1.getStartTime()
            .isAfter(task2.getStartTime())) ? 1 : -1);

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
        Optional<Integer> taskIdOpt = epicTasks.values().stream()
                .filter(e -> e.equals(epic))
                .findFirst()
                .map(foundEpic -> {
                    foundEpic.addSubTaskToEpic(newSubTask);
                    newSubTask.setEpic(foundEpic);
                    subTasks.put(taskId++, newSubTask);
                    checkEpicStatus(foundEpic);

                    return taskId;
                });

        return taskIdOpt.orElse(-1);
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
        epicTasks.values().forEach(epic -> epic.getSubTasks().clear());

        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));

        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epicTasks.get(id));

        return epicTasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subTasks.get(id));

        return subTasks.get(id);
    }

    @Override
    public List<SubTask> getSubTaskOfEpic(Epic epic) {
        return subTasks.values().stream()
                .filter(subTask -> subTask.getEpic().equals(epic))
                .collect(Collectors.toList());
    }

    @Override
    public void updateTask(Task task, int id) {
        tasks.put(id, task);
    }

    @Override
    public void updateEpicTask(Epic epic, int id) {
        Epic oldEpic = epicTasks.get(id);

        subTasks.values().stream()
                        .filter(subTask -> subTask.getEpic().equals(oldEpic))
                        .forEach(subTask -> subTask.setEpic(epic));

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
        historyManager.remove(tasks.get(id).getTaskId());
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        historyManager.remove(epicTasks.get(id).getTaskId());

        List<Integer> tempArr = subTasks.keySet().stream()
                .filter(key -> subTasks.get(key).getEpic().equals(epicTasks.get(id)))
                .toList();

        tempArr.forEach(subTasks::remove);

        epicTasks.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        historyManager.remove(subTasks.get(id).getTaskId());
        Epic tempEpic = subTasks.get(id).getEpic();

        epicTasks.values().stream()
                .filter(epic -> subTasks.get(id).getEpic().equals(epic))
                .forEach(epic -> epic.getSubTasks().remove(subTasks.get(id)));

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

        for (SubTask tempArrSubTask : tempArrSubTasks) {
            if (tempArrSubTask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                for (Integer epicKeys : epicTasks.keySet()) {
                    if (epicTasks.get(epicKeys).equals(tempArrSubTask.getEpic())) {
                        epicTasks.get(epicKeys).setTaskStatus(TaskStatus.IN_PROGRESS);
                        return;
                    }
                }

                return;
            } else if (tempArrSubTask.getTaskStatus() == TaskStatus.DONE) {
                for (Integer epicKeys : epicTasks.keySet()) {
                    if (epicTasks.get(epicKeys).equals(tempArrSubTask.getEpic())) {
                        epicTasks.get(epicKeys).setTaskStatus(TaskStatus.DONE);
                        break;
                    }
                }
            } else {
                for (Integer epicKeys : epicTasks.keySet()) {
                    if (epicTasks.get(epicKeys).equals(tempArrSubTask.getEpic())) {
                        epicTasks.get(epicKeys).setTaskStatus(TaskStatus.NEW);
                        break;
                    }
                }
            }
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        sortedTasks.clear();

        sortedTasks.addAll(tasks.values());

        sortedTasks.addAll(subTasks.values());

        return sortedTasks;
    }
}
