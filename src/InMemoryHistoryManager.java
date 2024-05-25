import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    //private final List<Task> history = new ArrayList<>();

    public Node<Task> head;
    public Node<Task> tail;
    @Override
    public void add(Task task) {
        if (history.isEmpty()) {
            Node<Task> newNode = new Node<>(task);

            head = tail = newNode;

            history.put(task.getTaskId(), newNode);
        } else if (history.containsKey(task.getTaskId())) {
            if (removeNode(history.get(task.getTaskId())) == 1) {
                Node<Task> newNode = new Node<>(task);

                linkLast(tail, newNode);

                history.put(task.getTaskId(), newNode);
            }
        } else {
            Node<Task> newNode = new Node<>(task);

            linkLast(tail, newNode);

            history.put(task.getTaskId(), newNode);
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

    public int removeNode(Node<Task> task) {
        if (task == tail) {
            if (history.size() == 1) {
                return 0;
            }

            tail = task.prev;
            tail.next = null;
        } else if (task == head) {
            head = task.next;
            head.prev = null;
        } else {
            Node<Task> prevElement = task.prev;
            Node<Task> nextElement = task.next;

            nextElement.prev = prevElement;
            prevElement.next = nextElement;
        }

        return 1;
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}