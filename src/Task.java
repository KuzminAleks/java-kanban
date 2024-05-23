import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private int id;
    private TaskStatus taskStatus;

    Task() {

    }

    Task(String newTaskName, String newDescription, TaskStatus newTaskStatus) {
        taskName = newTaskName;
        description = newDescription;
        id = hashCode();
        taskStatus = newTaskStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public int getTaskId() {
        return id;
    }

    public void setTaskStatus(TaskStatus newTaskStatus) {
        taskStatus = newTaskStatus;
    }

    @Override
    public int hashCode() {
        int hash = 17;

        if (taskName != null) {
            hash = taskName.hashCode();
        }

        hash *= 7;

        if (description != null) {
            hash += description.hashCode();
        }

        return Math.abs(hash);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Task someTask = (Task) obj;
        return id == someTask.id && Objects.equals(taskName, someTask.taskName)
                && Objects.equals(description, someTask.description);
    }
}
