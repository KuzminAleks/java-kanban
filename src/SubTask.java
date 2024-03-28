public class SubTask extends Task {
    private String taskName;
    private String description;
    private int taskId;
    private TaskStatus taskStatus;

    SubTask(String newName, String newDescription, TaskStatus newTaskStatus) {
        taskName = newName;
        description = newDescription;
        taskId = hashCode();
        taskStatus = newTaskStatus;
    }

    @Override
    public int hashCode() {
        int code = 29;

        if (taskName != null) {
            code += taskName.hashCode();
        }

        code *= 41;

        if (description != null) {
            code += description.hashCode();
        }

        return code;
    }
}
