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

/**
 * Provides the core command-processing logic for Whimsy Bot.
 *
 * <p>The chatbot can be used by either the console UI or the JavaFX GUI. Both
 * interfaces call {@link #getResponse(String)} so that command behavior is
 * kept in one place.</p>
 */
public class WhimsyBot {
    private final TaskList tasks;
    private final Storage storage;

    /** Creates a chatbot and loads its saved tasks. */
    public WhimsyBot() {
        storage = new Storage();
        tasks = new TaskList(storage.load());
    }

    /** Processes one command and returns the response to display.
     *
     * @param input the complete command entered by the user
     * @return the chatbot's response
     */
    public String getResponse(String input) {
        String[] parsedCommand = Parser.parse(input);
        String commandWord = parsedCommand[0];
        String arguments = parsedCommand[1];

        try {
            switch (CommandType.fromString(commandWord)) {
            case BYE:
                return "Bye. Hope to see you again soon!";
            case LIST:
                return getTaskListResponse();
            case FIND:
                return getFindResponse(arguments);
            case TAG:
            case UNTAG:
                return processTagCommand(commandWord, arguments);
            case MARK:
                return processMarkCommand(arguments, true);
            case UNMARK:
                return processMarkCommand(arguments, false);
            case DELETE:
                return processDeleteCommand(arguments);
            case TODO:
                return processTodoCommand(arguments);
            case DEADLINE:
                return processDeadlineCommand(arguments);
            case EVENT:
                return processEventCommand(arguments);
            default:
                return "";
            }
        } catch (WhimsyBotException e) {
            return e.getMessage();
        }
    }

    /** Runs the original console version of Whimsy Bot. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        WhimsyBot bot = new WhimsyBot();
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();
            ui.show(bot.getResponse(command));
            ui.showLine();
            if (command.trim().equals("bye")) {
                return;
            }
        }
    }

    private String getTaskListResponse() {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            response.append(System.lineSeparator()).append(i + 1).append('.').append(tasks.get(i));
        }
        return response.toString();
    }

    private String getFindResponse(String keyword) throws WhimsyBotException {
        if (keyword.isEmpty()) {
            throw new WhimsyBotException("OOPS!!! Please specify a keyword to find.");
        }
        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        int match = 1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                response.append(System.lineSeparator()).append(match++).append('.').append(tasks.get(i));
            }
        }
        return response.toString();
    }

    private String processTagCommand(String commandWord, String arguments) throws WhimsyBotException {
        String[] parts = arguments.split(" ", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new WhimsyBotException("OOPS!!! Please specify a task number and a tag.");
        }
        int taskNumber = parseTaskNumber(parts[0], commandWord, tasks.size());
        String tag = parts[1].trim();
        if (tag.startsWith("#")) {
            tag = tag.substring(1);
        }
        if (!tag.matches("[A-Za-z0-9_-]+")) {
            throw new WhimsyBotException(
                    "OOPS!!! Tags may contain only letters, numbers, underscores, and hyphens.");
        }
        boolean isAdding = commandWord.equals("tag");
        if (isAdding) {
            tasks.get(taskNumber - 1).addTag(tag);
        } else {
            tasks.get(taskNumber - 1).removeTag(tag);
        }
        saveTasks();
        return (isAdding ? "Added" : "Removed") + " tag #" + tag + "."
                + System.lineSeparator() + "  " + tasks.get(taskNumber - 1);
    }

    private String processMarkCommand(String argument, boolean isMarking) throws WhimsyBotException {
        int taskNumber = parseTaskNumber(argument, isMarking ? "mark" : "unmark", tasks.size());
        if (isMarking) {
            tasks.mark(taskNumber - 1);
        } else {
            tasks.unmark(taskNumber - 1);
        }
        saveTasks();
        String response = isMarking
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return response + System.lineSeparator() + "  " + tasks.get(taskNumber - 1);
    }

    private String processDeleteCommand(String argument) throws WhimsyBotException {
        int taskNumber = parseTaskNumber(argument, "delete", tasks.size());
        Task removedTask = tasks.delete(taskNumber - 1);
        saveTasks();
        return "Noted. I've removed this task:" + System.lineSeparator() + "  " + removedTask
                + System.lineSeparator() + "Now you have " + tasks.size() + " tasks in the list.";
    }

    private String processTodoCommand(String arguments) throws WhimsyBotException {
        if (arguments.isEmpty()) {
            throw new WhimsyBotException("OOPS!!! The description of a todo cannot be empty.");
        }
        checkListNotFull();
        tasks.add(new Todo(arguments));
        return saveAddedTask(tasks.get(tasks.size() - 1));
    }

    private String processDeadlineCommand(String arguments) throws WhimsyBotException {
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
                    "OOPS!!! Please specify a deadline, e.g. 'deadline " + description + " /by Sunday'.");
        }
        checkListNotFull();
        tasks.add(new Deadline(description, parts[1].trim()));
        return saveAddedTask(tasks.get(tasks.size() - 1));
    }

    private String processEventCommand(String arguments) throws WhimsyBotException {
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
        checkListNotFull();
        tasks.add(new Event(description, times[0].trim(), times[1].trim()));
        return saveAddedTask(tasks.get(tasks.size() - 1));
    }

    private String saveAddedTask(Task task) {
        saveTasks();
        return "Got it. I've added this task:" + System.lineSeparator() + "  " + task
                + System.lineSeparator() + "Now you have " + tasks.size() + " tasks in the list.";
    }

    private int parseTaskNumber(String argument, String commandName, int taskCount)
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

    private void checkListNotFull() throws WhimsyBotException {
        if (!tasks.canAdd()) {
            throw new WhimsyBotException("OOPS!!! Your task list is full, I can't add any more tasks.");
        }
    }

    private void saveTasks() {
        storage.save(tasks.toArray(), tasks.size());
    }
}
