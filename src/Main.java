public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");


        TaskManager taskManager = new InMemoryTaskManager();
//        taskManager.addTask(new Task("1 Task", "Some description", TaskStatus.NEW));
//        taskManager.addTask(new Task("2 Task", "Some description", TaskStatus.NEW));
//        taskManager.addTask(new Task("3 Task", "Some description", TaskStatus.NEW));
        //System.out.println(taskManager.getAllSimpleTasks());
        System.out.println(taskManager.getAllTasks());
//        HashMap<Integer, Task> simpleTasksMap = taskManager.getSimpleTasksMap();
//        for (Integer key : simpleTasksMap.keySet()) {
//            System.out.println(taskManager.getSimpleTaskById(key));
//        }
        //System.out.println();
//        taskManager.deleteAllSimpleTasks();
//        for (Integer key : tasks.keySet()) {
//            System.out.println(tasks.get(key).getTaskName() + " - " + tasks.get(key).getDescription());
//        }

        //System.out.println(taskManager.getSimpleTaskById());
    }
}
