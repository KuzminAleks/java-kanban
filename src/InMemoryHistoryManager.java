import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Task> lastSeenTasks = new LinkedHashMap<>();
    @Override
    public void add(int id, Task task) {
        lastSeenTasks.put(id, task);
    }

    @Override
    public Map<Integer, Task> getHistory() {
        return lastSeenTasks;
    }
}
