package whimsybot;

import whimsybot.parser.CommandType;
import whimsybot.parser.Parser;
import whimsybot.storage.Storage;
import whimsybot.task.Deadline;
import whimsybot.task.Event;
import whimsybot.task.Task;
import whimsybot.task.TaskList;
import whimsybot.task.Todo;
import whimsybot.ui.Ui;

import java.util.List;

/**
 * Starts the Whimsy Bot chatbot application.
 */
public class WhimsyBot {
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage();
        TaskList tasks = new TaskList(storage.load());
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();

            String[] parsedCommand = Parser.parse(command);
            String commandWord = parsedCommand[0];
            String arguments = parsedCommand[1];

            try {
                switch (CommandType.fromString(commandWord)) {
                case BYE:
                    ui.show("Bye. Hope to see you again soon!");
                    ui.showLine();
                    return;
                case LIST:
                    ui.show("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        ui.show((i + 1) + "." + tasks.get(i));
                    }
                    break;
                case MARK: {
                    int taskNumber = parseTaskNumber(arguments, "mark", tasks.size());
                    tasks.mark(taskNumber - 1);
                    ui.show("Nice! I've marked this task as done:");
                    ui.show("  " + tasks.get(taskNumber - 1));
                    saveTasks(tasks);
                    break;
                }
                case UNMARK: {
                    int taskNumber = parseTaskNumber(arguments, "unmark", tasks.size());
                    tasks.unmark(taskNumber - 1);
                    ui.show("OK, I've marked this task as not done yet:");
                    ui.show("  " + tasks.get(taskNumber - 1));
                    saveTasks(tasks);
                    break;
                }
                case DELETE: {
                    int taskNumber = parseTaskNumber(arguments, "delete", tasks.size());
                    Task removedTask = tasks.delete(taskNumber - 1);
                    ui.show("Noted. I've removed this task:");
                    ui.show("  " + removedTask);
                    ui.show("Now you have " + tasks.size() + " tasks in the list.");
                    saveTasks(tasks);
                    break;
                }
                case TODO:
                    if (arguments.isEmpty()) {
                        throw new WhimsyBotException("OOPS!!! The description of a todo cannot be empty.");
                    }
                    checkListNotFull(tasks);
                    tasks.add(new Todo(arguments));
                    printAddedTask(ui, tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(tasks);
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
                    checkListNotFull(tasks);
                    tasks.add(new Deadline(description, parts[1].trim()));
                    printAddedTask(ui, tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(tasks);
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
                    checkListNotFull(tasks);
                    tasks.add(new Event(description, times[0].trim(), times[1].trim()));
                    printAddedTask(ui, tasks.get(tasks.size() - 1), tasks.size());
                    saveTasks(tasks);
                    break;
                }
                }
            } catch (WhimsyBotException e) {
                ui.show(e.getMessage());
            }
            ui.showLine();
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
     * @param tasks the task list to check
     * @throws WhimsyBotException if the list is already full
     */
    private static void checkListNotFull(TaskList tasks) throws WhimsyBotException {
        if (!tasks.canAdd()) {
            throw new WhimsyBotException("OOPS!!! Your task list is full, I can't add any more tasks.");
        }
    }

    /**
     * Displays the confirmation shown after adding a task.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks now in the list
     */
    private static void printAddedTask(Ui ui, Task task, int taskCount) {
        ui.show("Got it. I've added this task:");
        ui.show("  " + task);
        ui.show("Now you have " + taskCount + " tasks in the list.");
    }

    /** Saves the task list without stopping the chatbot if disk storage is unavailable. */
    private static void saveTasks(TaskList tasks) {
        new Storage().save(tasks.toArray(), tasks.size());
    }
}
