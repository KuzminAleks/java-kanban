package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import Enums.TaskStatus;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(String newTaskName, String newDescription, TaskStatus newTaskStatus, Duration newDuration, LocalDateTime newLocalDateTime) {
        super(newTaskName, newDescription, newTaskStatus, newDuration, newLocalDateTime);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
