import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File dataFile;

    public FileBackedTaskManager(File file) {
        dataFile = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fbManager = new FileBackedTaskManager(file);
        fbManager.loadFromFile();
        return fbManager;
    }

    public void loadFromFile() {
        try (FileReader fr = new FileReader(dataFile); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                String line = br.readLine();

                fromString(line);
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("My error!");
        }
    }

    public void save() {
        try {
            //Path myPath = Paths.get("D:\\Java_Projects\\java-kanban\\saveState.txt");

            FileWriter fw = new FileWriter(dataFile);

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

    public void fromString(String line) {
        String[] param = line.split(",");

        if (param[1].equals(TaskType.TASK.name())) {
            if (param[4].equals(TaskStatus.NEW.name())) {
                super.addTask(new Task(param[2], param[3], TaskStatus.NEW));
            } else if (param[4].equals(TaskStatus.IN_PROGRESS.name())) {
                super.addTask(new Task(param[2], param[3], TaskStatus.IN_PROGRESS));
            } else {
                super.addTask(new Task(param[2], param[3], TaskStatus.DONE));
            }
        } else if (param[1].equals(TaskType.EPIC.name())) {
            super.addEpicTask(new Epic(param[2], param[4]));
        } else if (param[1].equals(TaskType.SUBTASK.name())) {
            if (epicTasks.containsKey(Integer.valueOf(param[5]))) {
                if (param[3].equals(TaskStatus.NEW.name())) {
                    super.addSubTask(epicTasks.get(Integer.valueOf(param[5])), new SubTask(param[2], param[4], TaskStatus.NEW));
                } else if (param[3].equals(TaskStatus.IN_PROGRESS.name())) {
                    super.addSubTask(epicTasks.get(Integer.valueOf(param[5])), new SubTask(param[2], param[4], TaskStatus.IN_PROGRESS));
                } else {
                    super.addSubTask(epicTasks.get(Integer.valueOf(param[5])), new SubTask(param[2], param[4], TaskStatus.DONE));
                }
            }
        }
    }
}

class ManagerSaveException extends RuntimeException {
    ManagerSaveException() {

    }

    ManagerSaveException(String par) {
        System.out.println(par);
    }
}