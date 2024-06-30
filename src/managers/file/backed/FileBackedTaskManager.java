package managers.file.backed;

import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import tasks.task.Task;
import tasks.epic.Epic;
import tasks.subtask.SubTask;
import managers.inmemory.InMemoryTaskManager;
import task.enums.TaskStatus;
import task.enums.TaskType;
import save.exception.ManagerSaveException;

public class FileBackedTaskManager extends InMemoryTaskManager {
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

    private void save() {
        try {
            FileWriter fw = new FileWriter(dataFile);

            fw.write("id,type,name,status,description,duration,date,epic\n");

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

                fw.write(keys + "," + toString(subTasks.get(keys)) + "," + epicId + ",\n");
            }

            fw.close();
        } catch (IOException exp) {
            throw new ManagerSaveException("My error!");
        }
    }

    @Override
    public int addTask(Task task) {
        if (!isIntersect(task)) {
            int id = super.addTask(task);
            save();
            return id;
        }

        return -1;
    }

    @Override
    public int addEpicTask(Epic newEpic) {
        if (!isIntersect(newEpic)) {
            int id = super.addEpicTask(newEpic);
            save();
            return id;
        }

        return -1;
    }

    @Override
    public int addSubTask(Epic epic, SubTask newSubTask) {
        if (!isIntersect(newSubTask)) {
            int id = super.addSubTask(epic, newSubTask);
            save();
            return id;
        }

        return -1;
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
    public boolean updateTask(Task task, int id) {
        if (!isIntersect(task)) {
            super.updateTask(task, id);
            save();
            return true;
        }

        return false;
    }

    @Override
    public boolean updateEpicTask(Epic epic, int id) {
        if (!isIntersect(epic)) {
            super.updateEpicTask(epic, id);
            save();

            return true;
        }

        return false;
    }

    @Override
    public boolean updateSubTask(SubTask subTask, int id) {
        if (!isIntersect(subTask)) {
            super.updateSubTask(subTask, id);
            save();

            return true;
        }

        return false;
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean isRemoved = super.deleteTaskById(id);
        save();

        return isRemoved;
    }

    @Override
    public boolean deleteEpicById(int id) {
        boolean isRemoved = super.deleteEpicById(id);
        save();
        return isRemoved;
    }

    @Override
    public boolean deleteSubTaskById(int id) {
        boolean isRemoved = super.deleteSubTaskById(id);
        save();

        return isRemoved;
    }

    public String toString(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC + "," + task.getTaskName() + "," + task.getTaskStatus() + "," + task.getDescription()
                    + "," + task.getDuration().toMinutes() + "," + task.getStartTime();
        } else if (task instanceof SubTask) {
            return TaskType.SUBTASK + "," + task.getTaskName() + "," + task.getTaskStatus() + ","
                    + task.getDescription() + "," + task.getDuration().toMinutes() + "," + task.getStartTime();
        } else {
            return TaskType.TASK + "," + task.getTaskName() + "," + task.getTaskStatus() + "," + task.getDescription()
                    + task.getDescription() + "," + task.getDuration().toMinutes() + "," + task.getStartTime();
        }
    }

    public void fromString(String line) {
        String[] param = line.split(",");

        if (param[1].equals(TaskType.TASK.name())) {
            if (param[4].equals(TaskStatus.NEW.name())) {
                super.addTask(new Task(param[2], param[3], TaskStatus.NEW, Duration.ofMinutes(Long.parseLong(param[5])), LocalDateTime.parse(param[6])));
            } else if (param[4].equals(TaskStatus.IN_PROGRESS.name())) {
                super.addTask(new Task(param[2], param[3], TaskStatus.IN_PROGRESS, Duration.ofMinutes(Long.parseLong(param[5])), LocalDateTime.parse(param[6])));
            } else {
                super.addTask(new Task(param[2], param[3], TaskStatus.DONE, Duration.ofMinutes(Long.parseLong(param[5])), LocalDateTime.parse(param[6])));
            }
        } else if (param[1].equals(TaskType.EPIC.name())) {
            super.addEpicTask(new Epic(param[2], param[4]));
        } else if (param[1].equals(TaskType.SUBTASK.name())) {
            if (epicTasks.containsKey(Integer.valueOf(param[7]))) {
                if (param[3].equals(TaskStatus.NEW.name())) {
                    super.addSubTask(epicTasks.get(Integer.valueOf(param[7])), new SubTask(param[2], param[4], TaskStatus.NEW, Duration.ofMinutes(Long.parseLong(param[5])), LocalDateTime.parse(param[6])));
                } else if (param[3].equals(TaskStatus.IN_PROGRESS.name())) {
                    super.addSubTask(epicTasks.get(Integer.valueOf(param[7])), new SubTask(param[2], param[4], TaskStatus.IN_PROGRESS, Duration.ofMinutes(Long.parseLong(param[5])), LocalDateTime.parse(param[6])));
                } else {
                    super.addSubTask(epicTasks.get(Integer.valueOf(param[7])), new SubTask(param[2], param[4], TaskStatus.DONE, Duration.ofMinutes(Long.parseLong(param[5])), LocalDateTime.parse(param[6])));
                }
            }
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public boolean isIntersect(Task task) {
        Set<Task> priorTasks = this.getPrioritizedTasks();

        return priorTasks.stream()
                .anyMatch(elem -> task.getEndTime().isAfter(elem.getStartTime())
                        && task.getStartTime().isBefore(elem.getEndTime()));
    }
}