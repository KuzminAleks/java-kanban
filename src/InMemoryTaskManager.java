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
        if (!isIntersect(newTask)) {
            tasks.put(taskId++, newTask);

            return taskId;
        }
         return -1;
    }

    @Override
    public int addEpicTask(Epic newEpic) {
        if (!isIntersect(newEpic)) {
            epicTasks.put(taskId++, newEpic);

            return taskId;
        }

        return -1;
    }

    @Override
    public int addSubTask(Epic epic, SubTask newSubTask) {
        Optional<Integer> taskIdOpt = epicTasks.values().stream()
                .filter(e -> e.equals(epic))
                .findFirst()
                .map(foundEpic -> {
                    if (!isIntersect(newSubTask)) {
                        foundEpic.addSubTaskToEpic(newSubTask);
                        newSubTask.setEpic(foundEpic);
                        subTasks.put(taskId++, newSubTask);
                        checkEpicStatus(foundEpic);

                        return taskId;
                    }

                    return null;
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
        if (tasks.get(id) == null) {
            return null;
        }

        historyManager.add(tasks.get(id));

        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epicTasks.get(id) == null) {
            return null;
        }

        historyManager.add(epicTasks.get(id));

        return epicTasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTasks.get(id) == null) {
            return null;
        }

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
    public boolean updateTask(Task task, int id) {
        if (!isIntersect(task)) {
            tasks.put(id, task);
            return true;
        }

        return false;
    }

    @Override
    public boolean updateEpicTask(Epic epic, int id) {
        if (!isIntersect(epic)) {
            Epic oldEpic = epicTasks.get(id);

            subTasks.values().stream()
                    .filter(subTask -> subTask.getEpic().equals(oldEpic))
                    .forEach(subTask -> subTask.setEpic(epic));

            epicTasks.put(id, epic);

            return true;
        }

        return false;
    }

    @Override
    public boolean updateSubTask(SubTask subTask, int id) {
        if (!isIntersect(subTask)) {
            subTask.setEpic(subTasks.get(id).getEpic());
            subTasks.put(id, subTask);

            checkEpicStatus(subTasks.get(id).getEpic());

            return true;
        }

        return false;
    }

    @Override
    public boolean deleteTaskById(int id) {
        if (tasks.get(id) == null) {
            return false;
        }

        historyManager.remove(tasks.get(id).getTaskId());
        tasks.remove(id);

        return true;
    }

    @Override
    public boolean deleteEpicById(int id) {
        if (epicTasks.get(id) == null) {
            return false;
        }

        historyManager.remove(epicTasks.get(id).getTaskId());

        List<Integer> tempArr = subTasks.keySet().stream()
                .filter(key -> subTasks.get(key).getEpic().equals(epicTasks.get(id)))
                .toList();

        tempArr.forEach(subTasks::remove);

        epicTasks.remove(id);

        return true;
    }

    @Override
    public boolean deleteSubTaskById(int id) {
        if (subTasks.get(id) == null) {
            return false;
        }

        historyManager.remove(subTasks.get(id).getTaskId());
        Epic tempEpic = subTasks.get(id).getEpic();

        epicTasks.values().stream()
                .filter(epic -> subTasks.get(id).getEpic().equals(epic))
                .forEach(epic -> epic.getSubTasks().remove(subTasks.get(id)));

        subTasks.remove(id);

        checkEpicStatus(tempEpic);

        return true;
    }

    @Override
    public void checkEpicStatus(Epic epic) {
        ArrayList<SubTask> tempArrSubTasks = new ArrayList<>();
        int flag = 0;

        for (Integer keys : subTasks.keySet()) {
            if (subTasks.get(keys).getEpic().equals(epic)) {
                tempArrSubTasks.add(subTasks.get(keys));
            }
        }

        for (SubTask tempArrSubTask : tempArrSubTasks) {
            if (tempArrSubTask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                tempArrSubTask.getEpic().setTaskStatus(TaskStatus.IN_PROGRESS);

                return;
            } else if (tempArrSubTask.getTaskStatus() == TaskStatus.DONE) {
                if (tempArrSubTask.getEpic().getTaskStatus().equals(TaskStatus.NEW) && flag > 0) {
                    tempArrSubTask.getEpic().setTaskStatus(TaskStatus.IN_PROGRESS);
                } else {
                    tempArrSubTask.getEpic().setTaskStatus(TaskStatus.DONE);
                }
            } else {
                if (tempArrSubTask.getEpic().getTaskStatus().equals(TaskStatus.DONE)) {
                    tempArrSubTask.getEpic().setTaskStatus(TaskStatus.IN_PROGRESS);
                }
            }

            ++flag;
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

    @Override
    public boolean isIntersect(Task task) {
        Set<Task> priorTasks = this.getPrioritizedTasks();

        return priorTasks.stream()
                .anyMatch(elem -> task.getEndTime().isAfter(elem.getStartTime())
                        && task.getStartTime().isBefore(elem.getEndTime()));
    }
}
