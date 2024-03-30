import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    Epic(String newEpicName, String newDescription) {
        super(newEpicName, newDescription, TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public void addSubTaskToEpic(SubTask newSubTask) {
        subTasks.add(newSubTask);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }
}
