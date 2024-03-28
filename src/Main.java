import java.util.HashMap;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");


        TaskManager taskManager = new TaskManager();
        taskManager.addSimpleTask(new Task("1 Task", "Some description", TaskStatus.NEW));
        taskManager.addSimpleTask(new Task("2 Task", "Some description", TaskStatus.NEW));
        taskManager.addSimpleTask(new Task("3 Task", "Some description", TaskStatus.NEW));
        //System.out.println(taskManager.getAllSimpleTasks());
        System.out.println(taskManager.getAllSimpleTasks());
        HashMap<Integer, Task> simpleTasksMap = taskManager.getSimpleTasksMap();
        for (Integer key : simpleTasksMap.keySet()) {
            System.out.println(taskManager.getSimpleTaskById(key));
        }
        System.out.println();
//        taskManager.deleteAllSimpleTasks();
//        for (Integer key : tasks.keySet()) {
//            System.out.println(tasks.get(key).getTaskName() + " - " + tasks.get(key).getDescription());
//        }

        //System.out.println(taskManager.getSimpleTaskById());
    }
}
