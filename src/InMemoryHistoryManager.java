import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> lastSeenTasks = new ArrayList<>();
    @Override
    public void add(Task task) {
        lastSeenTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return lastSeenTasks;
    }
}
