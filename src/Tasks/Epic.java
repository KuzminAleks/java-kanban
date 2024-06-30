package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import Enums.TaskStatus;

public class Epic extends Task {
    private final List<SubTask> subTasks;
    private LocalDateTime endTime;

    public Epic(String newEpicName, String newDescription) {
        super(newEpicName, newDescription, TaskStatus.NEW, Duration.ZERO, LocalDateTime.MIN);
        endTime = LocalDateTime.MIN;
        subTasks = new ArrayList<>();
    }

    public void addSubTaskToEpic(SubTask newSubTask) {
        subTasks.add(newSubTask);

        if (subTasks.size() < 2) {
            this.setStartTime(newSubTask.getStartTime());
            this.setDuration(newSubTask.getDuration());
            endTime = newSubTask.getEndTime();
            return;
        }

        for (SubTask task : subTasks) {
            this.setDuration(this.getDuration().plus(task.getDuration()));

            if (task.getStartTime().isBefore(this.getStartTime())) {
                this.setStartTime(task.getStartTime());
            } else if (task.getEndTime().isAfter(endTime)) {
                endTime = task.getEndTime();
            }
        }
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
