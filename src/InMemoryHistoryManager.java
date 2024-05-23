import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    //private final List<Task> history = new ArrayList<>();

    public Node<Task> head;
    public Node<Task> tail;
    private static int size = 0;
    @Override
    public void add(Task task) {
        if (size == 0) {
            Node<Task> newNode = new Node<>(task);

            head = tail = newNode;

            history.put(task.getTaskId(), newNode);

            ++size;
        } else if (history.containsKey(task.getTaskId())) {
            removeNode(history.get(task.getTaskId()));

            Node<Task> newNode = new Node<>(task);

            linkLast(tail, newNode);

            history.put(task.getTaskId(), newNode);
        } else {
            Node<Task> newNode = new Node<>(task);

            linkLast(tail, newNode);

            history.put(task.getTaskId(), newNode);

            ++size;
        }
    }

    public void linkLast(Node<Task> oldElement, Node<Task> newElement) {
        oldElement.next = newElement;
        newElement.prev = oldElement;

        tail = newElement;
    }

    public List<Task> getTasks() {
        List<Task> arrHistory = new ArrayList<>();
        Node<Task> tempHead = head;

        while (tempHead != null) {
            arrHistory.add(tempHead.data);
            tempHead = tempHead.next;
        }

        return arrHistory;
    }

    public void removeNode(Node<Task> task) {
        if (task == tail) {
            tail = task.prev;
        } else if (task == head) {
            head = task.next;
        } else {
            Node<Task> prevElement = task.prev;
            Node<Task> nextElement = task.next;

            nextElement.prev = prevElement;
            prevElement.next = nextElement;
        }

        --size;
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
        --size;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}