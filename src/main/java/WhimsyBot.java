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

        String[] tasks = new String[MAX_TASKS];
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
            }
            System.out.println(SEPARATOR);
        }
    }
}
