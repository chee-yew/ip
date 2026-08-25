import java.util.Scanner;

/**
 * Starts the Whimsy Bot chatbot application.
 */
public class WhimsyBot {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String banner = " _       ___     _                         \n"
                + "| |     / / |__ (_)_ __ ___  ___ _   _     \n"
                + "| | /| / /| '_ \\| | '_ ` _ \\/ __| | | |    \n"
                + "| |/ |/ / | | | | | | | | | \\__ \\ |_| |    \n"
                + "|__/|__/  |_| |_|_|_| |_| |_|___/\\__, |    \n"
                + "                                  |___/     \n"
                + " ____        _   \n"
                + "| __ )  ___ | |_ \n"
                + "|  _ \\ / _ \\| __|\n"
                + "| |_) | (_) | |_ \n"
                + "|____/ \\___/ \\__|\n";
        System.out.println(SEPARATOR);
        System.out.println(banner);
        System.out.println("Hello! I'm Whimsy Bot.");
        System.out.println("What can I do for you today?");
        System.out.println(SEPARATOR);

        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                tasks[taskNumber - 1].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[taskNumber - 1]);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                tasks[taskNumber - 1].unmarkAsDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[taskNumber - 1]);
            } else if (command.startsWith("todo ") && taskCount < MAX_TASKS) {
                tasks[taskCount] = new Todo(command.substring(5));
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
            } else if (command.startsWith("deadline ") && taskCount < MAX_TASKS) {
                String[] parts = command.substring(9).split(" /by ", 2);
                tasks[taskCount] = new Deadline(parts[0], parts[1]);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
            } else if (command.startsWith("event ") && taskCount < MAX_TASKS) {
                String[] descriptionAndTimes = command.substring(6).split(" /from ", 2);
                String[] times = descriptionAndTimes[1].split(" /to ", 2);
                tasks[taskCount] = new Event(descriptionAndTimes[0], times[0], times[1]);
                taskCount++;
                printAddedTask(tasks[taskCount - 1], taskCount);
            }
            System.out.println(SEPARATOR);
        }
    }

    /**
     * Displays the confirmation shown after adding a task.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks now in the list
     */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }
}
