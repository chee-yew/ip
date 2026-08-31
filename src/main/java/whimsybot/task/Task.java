package whimsybot.task;

/**
 * Represents a task in Whimsy Bot's task list.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
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

    /** Marks this task as complete. */
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
        return "[" + getStatusIcon() + "] " + description;
    }
}
