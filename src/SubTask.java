public class SubTask extends Task {
    private Epic epic;

    SubTask(String newTaskName, String newDescription, TaskStatus newTaskStatus) {
        super(newTaskName, newDescription, newTaskStatus);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
