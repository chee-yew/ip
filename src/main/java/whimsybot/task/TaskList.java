package whimsybot.task;

import java.util.List;

/** Stores and manages the tasks currently known to Whimsy Bot. */
public class TaskList {
    private static final int MAX_TASKS = 100;
    private final Task[] tasks;
    private int taskCount;

    /** Creates a task list containing the supplied saved tasks, up to its capacity. */
    public TaskList(List<Task> savedTasks) {
        tasks = new Task[MAX_TASKS];
        taskCount = Math.min(savedTasks.size(), MAX_TASKS);
        for (int i = 0; i < taskCount; i++) {
            tasks[i] = savedTasks.get(i);
        }
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return taskCount;
    }

    /** Returns whether another task can be added to this list. */
    public boolean canAdd() {
        return taskCount < MAX_TASKS;
    }

    /** Returns the task at a zero-based index. */
    public Task get(int index) {
        return tasks[index];
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks[taskCount] = task;
        taskCount++;
    }

    /** Removes and returns the task at a zero-based index. */
    public Task delete(int index) {
        Task removedTask = tasks[index];
        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[taskCount - 1] = null;
        taskCount--;
        return removedTask;
    }

    /** Marks the task at the given zero-based index as done. */
    public void mark(int index) {
        tasks[index].markAsDone();
    }

    /** Marks the task at the given zero-based index as not done. */
    public void unmark(int index) {
        tasks[index].unmarkAsDone();
    }

    /** Returns the tasks as an array and count pair for storage. */
    public Task[] toArray() {
        return tasks;
    }
}
