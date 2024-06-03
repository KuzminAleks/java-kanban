import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    public void save() {
        try {
            Path myPath = Paths.get("D:\\Java_Projects\\java-kanban\\test.txt");

            FileWriter fw = new FileWriter(myPath.toFile());

            fw.write("id,type,name,status,description,epic\n");

            for (Integer keys : tasks.keySet()) {
                fw.write(keys + "," + toString(tasks.get(keys)) + ",\n");
            }

            for (Integer keys : epicTasks.keySet()) {
                fw.write(keys + "," + toString(epicTasks.get(keys)) + ",\n");
            }

            for (Integer keys : subTasks.keySet()) {
                int epicId = -1;
                Epic epic = subTasks.get(keys).getEpic();

                for (Map.Entry<Integer, Epic> entry : epicTasks.entrySet()) {
                    if (entry.getValue().equals(epic)) {
                        epicId = entry.getKey();
                    }
                }

                fw.write(keys + "," + toString(subTasks.get(keys)) + epicId + ",\n");
            }

            fw.close();
        } catch (IOException exp) {
            throw new ManagerSaveException("My error!");
        }
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpicTask(Epic newEpic) {
        int id = super.addEpicTask(newEpic);
        save();
        return id;
    }

    @Override
    public int addSubTask(Epic epic, SubTask newSubTask) {
        int id = super.addSubTask(epic, newSubTask);
        save();
        return id;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void updateTask(Task task, int id) {
        super.updateTask(task, id);
        save();
    }

    @Override
    public void updateEpicTask(Epic epic, int id) {
        super.updateEpicTask(epic, id);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask, int id) {
        super.updateSubTask(subTask, id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    public String toString(Task task) {
        TaskType tp;

        if (task instanceof Epic) {
            tp = TaskType.EPIC;
        } else if (task instanceof SubTask) {
            tp = TaskType.SUBTASK;

            return tp + "," + task.getTaskName() + "," + task.getTaskStatus() + "," + task.getDescription() + ",";
        } else {
            tp = TaskType.TASK;
        }

        return tp + "," + task.getTaskName() + "," + task.getTaskStatus() + "," + task.getDescription();
    }

    public Task fromString(String str) {
        return null;
    }
}

class ManagerSaveException extends RuntimeException {
    ManagerSaveException() {

    }

    ManagerSaveException(String par) {
        System.out.println(par);
    }
}