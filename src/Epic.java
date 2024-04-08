import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    Epic(String newEpicName, String newDescription) {
        super(newEpicName, newDescription, TaskStatus.NEW);
        subTasks = new ArrayList<>();
    }

    public void addSubTaskToEpic(SubTask newSubTask) {
        subTasks.add(newSubTask);
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }
}
