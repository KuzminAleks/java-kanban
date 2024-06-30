package managers.interfaces;

import java.util.List;
import tasks.task.Task;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
