import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Epic epic;

    SubTask(String newTaskName, String newDescription, TaskStatus newTaskStatus, Duration newDuration, LocalDateTime newLocalDateTime) {
        super(newTaskName, newDescription, newTaskStatus, newDuration, newLocalDateTime);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
