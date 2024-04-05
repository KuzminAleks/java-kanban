import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> lastSeenTasks = new ArrayList<>();
    @Override
    public void add(Task task) {

    }

    @Override
    public ArrayList<Task> getHistory() {
        return lastSeenTasks;
    }
}
