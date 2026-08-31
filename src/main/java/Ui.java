import java.util.Scanner;

/** Handles Whimsy Bot's interaction with the user through the console. */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /** Displays the application's welcome message. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(" _       ___     _                         \n"
                + "| |     / / |__ (_)_ __ ___  ___ _   _     \n"
                + "| | /| / /| '_ \\| | '_ ` _ \\/ __| | | |    \n"
                + "| |/ |/ / | | | | | | | | | \\__ \\ |_| |    \n"
                + "|__/|__/  |_| |_|_|_| |_| |_|___/\\__, |    \n"
                + "                                  |___/     \n"
                + " ____        _   \n"
                + "| __ )  ___ | |_ \n"
                + "|  _ \\ / _ \\| __|\n"
                + "| |_) | (_) | |_ \n"
                + "|____/ \\___/ \\__|\n");
        show("Hello! I'm Whimsy Bot.");
        show("What can I do for you today?");
        showLine();
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the standard divider. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Displays a message. */
    public void show(String message) {
        System.out.println(message);
    }
}
