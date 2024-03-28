public class Task {
    private String taskName;
    private String description;
    private int taskId;
    private TaskStatus taskStatus;

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
        int code = 17;

        if (taskName != null) {
            code += taskName.hashCode();
        }

        code *= 37;

        if (description != null) {
            code += description.hashCode();
        }

        return code;
    }
}
