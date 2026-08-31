import java.util.List;

/**
 * Starts the Whimsy Bot chatbot application.
 */
public class WhimsyBot {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Task[] tasks = new Task[MAX_TASKS];
        Storage storage = new Storage();
        List<Task> savedTasks = storage.load();
        int taskCount = Math.min(savedTasks.size(), MAX_TASKS);
        for (int i = 0; i < taskCount; i++) {
            tasks[i] = savedTasks.get(i);
        }
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            String[] splitCommand = command.split(" ", 2);
            String commandWord = splitCommand[0];
            String arguments = splitCommand.length > 1 ? splitCommand[1].trim() : "";

            try {
                switch (CommandType.fromString(commandWord)) {
                case BYE:
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(SEPARATOR);
                    return;
                case LIST:
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                    break;
                case MARK: {
                    int taskNumber = parseTaskNumber(arguments, "mark", taskCount);
                    tasks[taskNumber - 1].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[taskNumber - 1]);
                    saveTasks(tasks, taskCount);
                    break;
                }
                case UNMARK: {
                    int taskNumber = parseTaskNumber(arguments, "unmark", taskCount);
                    tasks[taskNumber - 1].unmarkAsDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks[taskNumber - 1]);
                    saveTasks(tasks, taskCount);
                    break;
                }
                case DELETE: {
                    int taskNumber = parseTaskNumber(arguments, "delete", taskCount);
                    Task removedTask = tasks[taskNumber - 1];
                    for (int i = taskNumber - 1; i < taskCount - 1; i++) {
                        tasks[i] = tasks[i + 1];
                    }
                    tasks[taskCount - 1] = null;
                    taskCount--;
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println("Now you have " + taskCount + " tasks in the list.");
                    saveTasks(tasks, taskCount);
                    break;
                }
                case TODO:
                    if (arguments.isEmpty()) {
                        throw new WhimsyBotException("OOPS!!! The description of a todo cannot be empty.");
                    }
                    checkListNotFull(taskCount);
                    tasks[taskCount] = new Todo(arguments);
                    taskCount++;
                    printAddedTask(tasks[taskCount - 1], taskCount);
                    saveTasks(tasks, taskCount);
                    break;
                case DEADLINE: {
                    if (arguments.isEmpty()) {
                        throw new WhimsyBotException("OOPS!!! The description of a deadline cannot be empty.");
                    }
                    String[] parts = arguments.split(" /by ", 2);
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
                    saveTasks(tasks, taskCount);
                    break;
                }
                case EVENT: {
                    if (arguments.isEmpty()) {
                        throw new WhimsyBotException("OOPS!!! The description of an event cannot be empty.");
                    }
                    String[] descriptionAndTimes = arguments.split(" /from ", 2);
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
                    saveTasks(tasks, taskCount);
                    break;
                }
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

    /** Saves the task list without stopping the chatbot if disk storage is unavailable. */
    private static void saveTasks(Task[] tasks, int taskCount) {
        new Storage().save(tasks, taskCount);
    }
}
