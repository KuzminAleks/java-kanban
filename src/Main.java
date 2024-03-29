import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");


        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Task("Task", "Some description", TaskStatus.NEW));
        taskManager.addEpicTask(new Epic("1 Epic", "Some description"));
        taskManager.addEpicTask(new Epic("2 Epic", "Some description"));
        taskManager.addSubTask(new Epic("1 Epic", "Some description"), new SubTask("1 SubTask of 1 Epic", "Some Description", TaskStatus.NEW));
        taskManager.addSubTask(new Epic("1 Epic", "Some description"), new SubTask("2 SubTask of 1 Epic", "Some Description", TaskStatus.NEW));
        taskManager.addSubTask(new Epic("1 Epic", "Some description"), new SubTask("3 SubTask of 1 Epic", "Some Description", TaskStatus.NEW));
        taskManager.addSubTask(new Epic("2 Epic", "Some description"), new SubTask("1 SubTask of 2 Epic", "Some Description", TaskStatus.NEW));
//        taskManager.deleteSubTaskById(6);
//        taskManager.deleteEpicById(1);
        //taskManager.updateTask(new Task("Updated Task", "Some description", TaskStatus.NEW), 0);
        //System.out.println(taskManager.getAllEpicTasks().getFirst().getSubTasks().getFirst().taskName);
//        System.out.println(taskManager.getAllEpicTasks().getFirst().taskName);
//        System.out.println(taskManager.getAllEpicTasks().getFirst().description);
//        System.out.println(taskManager.getAllEpicTasks().getFirst().taskId);
//
//        System.out.println(taskManager.getAllEpicTasks().getLast().taskName);
//        System.out.println(taskManager.getAllEpicTasks().getLast().description);
//        System.out.println(taskManager.getAllEpicTasks().getLast().taskId);
        //System.out.println(taskManager.getAllEpicTasks().getLast().taskName);
        //taskManager.addSimpleTask(new Task("2 Task", "Some description", TaskStatus.NEW));
        //taskManager.addSimpleTask(new Task("3 Task", "Some description", TaskStatus.NEW));
        //System.out.println(taskManager.getAllSimpleTasks());
        //System.out.println(taskManager.getAllSimpleTasks());
        //HashMap<Integer, Task> simpleTasksMap = taskManager.getSimpleTasksMap();
//        for (Integer key : simpleTasksMap.keySet()) {
//            System.out.println(taskManager.getSimpleTaskById(key));
//        }
        //System.out.println(taskManager.getSimpleTaskById(1));
//        taskManager.deleteAllSimpleTasks();
//        for (Integer key : tasks.keySet()) {
//            System.out.println(tasks.get(key).getTaskName() + " - " + tasks.get(key).getDescription());
//        }

        //System.out.println(taskManager.getSimpleTaskById());
    }
}
