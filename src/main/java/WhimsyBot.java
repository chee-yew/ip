/**
 * Starts the Whimsy Bot chatbot application.
 */
public class WhimsyBot {
    private static final String SEPARATOR = "____________________________________________________________";

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
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }
}
