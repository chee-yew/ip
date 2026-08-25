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

            try {
                if (command.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int taskNumber = parseTaskNumber(command.substring(4).trim(), "mark", taskCount);
                    tasks[taskNumber - 1].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[taskNumber - 1]);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskNumber = parseTaskNumber(command.substring(6).trim(), "unmark", taskCount);
                    tasks[taskNumber - 1].unmarkAsDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks[taskNumber - 1]);
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int taskNumber = parseTaskNumber(command.substring(6).trim(), "delete", taskCount);
                    Task removedTask = tasks[taskNumber - 1];
                    for (int i = taskNumber - 1; i < taskCount - 1; i++) {
                        tasks[i] = tasks[i + 1];
                    }
                    tasks[taskCount - 1] = null;
                    taskCount--;
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println("Now you have " + taskCount + " tasks in the list.");
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new WhimsyBotException("OOPS!!! The description of a todo cannot be empty.");
                    }
                    checkListNotFull(taskCount);
                    tasks[taskCount] = new Todo(description);
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String rest = command.substring(8).trim();
                    if (rest.isEmpty()) {
                        throw new WhimsyBotException("OOPS!!! The description of a deadline cannot be empty.");
                    }
                    String[] parts = rest.split(" /by ", 2);
                    String description = parts[0].trim();
                    if (description.isEmpty()) {
                        throw new WhimsyBotException("OOPS!!! The description of a deadline cannot be empty.");
                    }
                    if (parts.length < 2 || parts[1].trim().isEmpty()) {
                        throw new WhimsyBotException(
                                "OOPS!!! Please specify a deadline, e.g. 'deadline "
                                        + description + " /by Sunday'.");
                    }
                    checkListNotFull(taskCount);
                    tasks[taskCount] = new Deadline(description, parts[1].trim());
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                } else if (command.equals("event") || command.startsWith("event ")) {
                    String rest = command.substring(5).trim();
                    if (rest.isEmpty()) {
                        throw new WhimsyBotException("OOPS!!! The description of an event cannot be empty.");
                    }
                    String[] descriptionAndTimes = rest.split(" /from ", 2);
                    String description = descriptionAndTimes[0].trim();
                    if (description.isEmpty()) {
                        throw new WhimsyBotException("OOPS!!! The description of an event cannot be empty.");
                    }
                    if (descriptionAndTimes.length < 2 || descriptionAndTimes[1].trim().isEmpty()) {
                        throw new WhimsyBotException(
                                "OOPS!!! Please specify the event's start and end, e.g. 'event "
                                        + description + " /from Monday 2pm /to 4pm'.");
                    }
                    String[] times = descriptionAndTimes[1].split(" /to ", 2);
                    if (times.length < 2 || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
                        throw new WhimsyBotException(
                                "OOPS!!! Please specify the event's start and end, e.g. 'event "
                                        + description + " /from Monday 2pm /to 4pm'.");
                    }
                    checkListNotFull(taskCount);
                    tasks[taskCount] = new Event(description, times[0].trim(), times[1].trim());
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                } else {
                    throw new WhimsyBotException("OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
            } catch (WhimsyBotException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(SEPARATOR);
        }
    }

    /**
     * Parses the task number given as an argument to a mark/unmark command.
     *
     * @param argument the text following the command word, e.g. {@code "2"}
     * @param commandName the command word, used in error messages
     * @param taskCount the number of tasks currently in the list
     * @return the 1-based task number, guaranteed to be a valid index
     * @throws WhimsyBotException if the argument is missing, not a number, or out of range
     */
    private static int parseTaskNumber(String argument, String commandName, int taskCount)
            throws WhimsyBotException {
        if (argument.isEmpty()) {
            throw new WhimsyBotException("OOPS!!! Please specify which task number to " + commandName + ".");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new WhimsyBotException("OOPS!!! The task number must be a whole number.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new WhimsyBotException("OOPS!!! There is no task number " + taskNumber + " in your list.");
        }
        return taskNumber;
    }

    /**
     * Ensures the task list has room for one more task.
     *
     * @param taskCount the number of tasks currently in the list
     * @throws WhimsyBotException if the list is already full
     */
    private static void checkListNotFull(int taskCount) throws WhimsyBotException {
        if (taskCount >= MAX_TASKS) {
            throw new WhimsyBotException("OOPS!!! Your task list is full, I can't add any more tasks.");
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
