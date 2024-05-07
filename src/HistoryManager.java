import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface HistoryManager {
    void add(int id,Task task);
    Map<Integer, Task> getHistory();
}
