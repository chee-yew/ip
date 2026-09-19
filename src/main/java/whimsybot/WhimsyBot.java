package whimsybot;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import whimsybot.parser.CommandType;
import whimsybot.parser.Parser;
import whimsybot.storage.Storage;
import whimsybot.storage.StorageException;
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
    private static final Pattern ISO_DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private final TaskList tasks;
    private final Storage storage;
    private String startupWarning;

    /** Creates a chatbot and loads its saved tasks. */
    public WhimsyBot() {
        storage = new Storage();
        List<Task> savedTasks;
        try {
            savedTasks = storage.load();
        } catch (StorageException e) {
            savedTasks = List.of();
            startupWarning = e.getMessage();
        }
        tasks = new TaskList(savedTasks);
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
                validateNoArguments(commandWord, arguments);
                return includeStartupWarning("Bye. Hope to see you again soon!");
            case LIST:
                validateNoArguments(commandWord, arguments);
                return includeStartupWarning(getTaskListResponse());
            case FIND:
                return includeStartupWarning(getFindResponse(arguments));
            case HELP:
                validateNoArguments(commandWord, arguments);
                return includeStartupWarning(getHelpResponse());
            case TAG:
            case UNTAG:
                return includeStartupWarning(processTagCommand(commandWord, arguments));
            case MARK:
                return includeStartupWarning(processMarkCommand(arguments, true));
            case UNMARK:
                return includeStartupWarning(processMarkCommand(arguments, false));
            case DELETE:
                return includeStartupWarning(processDeleteCommand(arguments));
            case TODO:
                return includeStartupWarning(processTodoCommand(arguments));
            case DEADLINE:
                return includeStartupWarning(processDeadlineCommand(arguments));
            case EVENT:
                return includeStartupWarning(processEventCommand(arguments));
            default:
                return "";
            }
        } catch (WhimsyBotException e) {
            return includeStartupWarning(e.getMessage());
        }
    }

    private String includeStartupWarning(String response) {
        if (startupWarning == null) {
            return response;
        }
        String warning = startupWarning;
        startupWarning = null;
        return warning + System.lineSeparator() + response;
    }

    private void validateNoArguments(String commandWord, String arguments) throws WhimsyBotException {
        if (!arguments.isEmpty()) {
            throw new WhimsyBotException(
                    "OOPS!!! The " + commandWord + " command does not accept any arguments.");
        }
    }

    /** Runs the original console version of Whimsy Bot. */
    public static void main(String... args) {
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
        return IntStream.range(0, tasks.size())
                .mapToObj(index -> System.lineSeparator() + (index + 1) + "." + tasks.get(index))
                .collect(Collectors.joining("", "Here are the tasks in your list:", ""));
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

    /** Returns concise in-app guidance for the commands supported by Whimsy Bot. */
    private String getHelpResponse() {
        return String.join(System.lineSeparator(),
                "Here is how to use Whimsy Bot:",
                "  todo DESCRIPTION - add a task without a date",
                "  deadline DESCRIPTION /by DATE - add a task with a deadline",
                "  event DESCRIPTION /from START /to END - add an event",
                "  list - show all tasks",
                "  find KEYWORD - find tasks containing a keyword",
                "  mark NUMBER - mark a task as done",
                "  unmark NUMBER - mark a task as not done",
                "  tag NUMBER TAG - add a tag to a task",
                "  untag NUMBER TAG - remove a tag from a task",
                "  delete NUMBER - remove a task",
                "  help - show this help page",
                "  bye - exit Whimsy Bot");
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
        Todo task = new Todo(arguments);
        checkForDuplicate(task);
        tasks.add(task);
        return saveAddedTask(tasks.get(tasks.size() - 1));
    }

    private String processDeadlineCommand(String arguments) throws WhimsyBotException {
        if (arguments.isEmpty()) {
            throw new WhimsyBotException("OOPS!!! The description of a deadline cannot be empty.");
        }
        String[] parts = arguments.split("(?i)\\s+/by\\s+", -1);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new WhimsyBotException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (parts.length != 2 || parts[1].trim().isEmpty()) {
            throw new WhimsyBotException(
                    "OOPS!!! Please specify a deadline, e.g. 'deadline " + description + " /by Sunday'.");
        }
        String deadline = parts[1].trim();
        validateDateValue(deadline, "deadline");
        checkListNotFull();
        Deadline task = new Deadline(description, deadline);
        checkForDuplicate(task);
        tasks.add(task);
        return saveAddedTask(tasks.get(tasks.size() - 1));
    }

    private String processEventCommand(String arguments) throws WhimsyBotException {
        if (arguments.isEmpty()) {
            throw new WhimsyBotException("OOPS!!! The description of an event cannot be empty.");
        }
        String[] descriptionAndTimes = arguments.split("(?i)\\s+/from\\s+", -1);
        String description = descriptionAndTimes[0].trim();
        if (description.isEmpty()) {
            throw new WhimsyBotException("OOPS!!! The description of an event cannot be empty.");
        }
        if (descriptionAndTimes.length != 2 || descriptionAndTimes[1].trim().isEmpty()) {
            throw new WhimsyBotException(
                    "OOPS!!! Please specify the event's start and end, e.g. 'event "
                            + description + " /from Monday 2pm /to 4pm'.");
        }
        String[] times = descriptionAndTimes[1].split("(?i)\\s+/to\\s+", -1);
        if (times.length != 2 || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
            throw new WhimsyBotException(
                    "OOPS!!! Please specify the event's start and end, e.g. 'event "
                            + description + " /from Monday 2pm /to 4pm'.");
        }
        String from = times[0].trim();
        String to = times[1].trim();
        validateDateValue(from, "event start");
        validateDateValue(to, "event end");
        validateEventOrder(from, to);
        checkListNotFull();
        Event task = new Event(description, from, to);
        checkForDuplicate(task);
        tasks.add(task);
        return saveAddedTask(tasks.get(tasks.size() - 1));
    }

    private String saveAddedTask(Task task) throws WhimsyBotException {
        saveTasks();
        return "Got it. I've added this task:" + System.lineSeparator() + "  " + task
                + System.lineSeparator() + "Now you have " + tasks.size() + " tasks in the list.";
    }

    private void checkForDuplicate(Task candidate) throws WhimsyBotException {
        for (int i = 0; i < tasks.size(); i++) {
            Task existing = tasks.get(i);
            if (isDuplicate(existing, candidate)) {
                throw new WhimsyBotException(
                        "OOPS!!! This task is already in your list. Please add a different task.");
            }
        }
    }

    private boolean isDuplicate(Task first, Task second) {
        if (first.getClass() != second.getClass()
                || !first.getDescription().equals(second.getDescription())) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getBy().equals(secondDeadline.getBy());
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return firstEvent.getFrom().equals(secondEvent.getFrom())
                    && firstEvent.getTo().equals(secondEvent.getTo());
        }
        return true;
    }

    private void validateDateValue(String value, String fieldName) throws WhimsyBotException {
        if (!ISO_DATE_PATTERN.matcher(value).matches()) {
            return;
        }
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new WhimsyBotException("OOPS!!! The " + fieldName
                    + " must be a valid date, such as 2026-09-20.");
        }
    }

    private void validateEventOrder(String from, String to) throws WhimsyBotException {
        if (!ISO_DATE_PATTERN.matcher(from).matches() || !ISO_DATE_PATTERN.matcher(to).matches()) {
            return;
        }
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        if (!fromDate.isBefore(toDate)) {
            throw new WhimsyBotException(
                    "OOPS!!! An event's start date must be before its end date.");
        }
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

    private void saveTasks() throws WhimsyBotException {
        storage.save(tasks.toArray(), tasks.size());
    }
}
