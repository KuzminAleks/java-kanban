import java.util.Objects;

public class Task {
    protected String taskName;
    protected String description;
    protected int taskId;
    protected TaskStatus taskStatus;

    Task() {

    }

    Task(String newTaskName, String newDescription, TaskStatus newTaskStatus) {
        taskName = newTaskName;
        description = newDescription;
        taskId = hashCode();
        taskStatus = newTaskStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
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
        return taskId == someTask.taskId && Objects.equals(taskName, someTask.taskName)
                && Objects.equals(description, someTask.description);
    }
}
