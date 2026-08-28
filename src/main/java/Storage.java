import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

/** Saves and loads Whimsy Bot tasks using a file relative to the project root. */
public class Storage {
    private static final Path FILE_PATH = Paths.get("data", "whimsybot.txt");

    /** Writes the current tasks, creating the parent directory when necessary. */
    public void save(Task[] tasks, int taskCount) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            List<String> lines = new java.util.ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                lines.add(serialize(tasks[i]));
            }
            Files.write(FILE_PATH, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // The chatbot can continue running even when the file cannot be written.
        }
    }

    private static String serialize(Task task) {
        String type;
        List<String> fields = new ArrayList<>();
        if (task instanceof Deadline deadline) {
            type = "D";
            fields.add(deadline.getBy());
        } else if (task instanceof Event event) {
            type = "E";
            fields.add(event.getFrom());
            fields.add(event.getTo());
        } else {
            type = "T";
        }
        StringBuilder line = new StringBuilder(type)
                .append("|").append(task.isDone ? "1" : "0")
                .append("|").append(encode(task.description));
        for (String field : fields) {
            line.append("|").append(encode(field));
        }
        return line.toString();
    }

    /** Loads valid tasks and ignores malformed records so one bad line cannot break startup. */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8)) {
                Task task = parse(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            return tasks;
        }
        return tasks;
    }

    private static Task parse(String line) {
        String[] parts = line.split("\\|", -1);
        try {
            if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))) {
                return null;
            }
            String description = decode(parts[2]);
            Task task;
            switch (parts[0]) {
            case "T":
                if (parts.length != 3) return null;
                task = new Todo(description);
                break;
            case "D":
                if (parts.length != 4) return null;
                task = new Deadline(description, decode(parts[3]));
                break;
            case "E":
                if (parts.length != 5) return null;
                task = new Event(description, decode(parts[3]), decode(parts[4]));
                break;
            default:
                return null;
            }
            if (parts[1].equals("1")) task.markAsDone();
            return task;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
