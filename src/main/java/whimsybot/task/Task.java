package whimsybot.task;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a task in Whimsy Bot's task list.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    private final Set<String> tags;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.tags = new LinkedHashSet<>();
    }

    /**
     * Returns the symbol used to show whether this task is complete.
     *
     * @return {@code "X"} when complete, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the task description for persistence and display-related operations. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task has been marked as complete. */
    public boolean isDone() {
        return isDone;
    }

    /** Adds a tag, preserving the order in which tags were added. */
    public void addTag(String tag) { tags.add(tag); }

    /** Removes a tag if it is present. */
    public void removeTag(String tag) { tags.remove(tag); }

    /** Returns this task's tags. */
    public Set<String> getTags() { return Collections.unmodifiableSet(tags); }

    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns the common, human-readable portion of every task.
     *
     * @return this task's completion status and description
     */
    @Override
    public String toString() {
        String tagText = tags.stream().map(tag -> "[#" + tag + "]").reduce("", String::concat);
        return "[" + getStatusIcon() + "] " + description + tagText;
    }
}
