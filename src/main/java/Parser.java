/** Parses the command word and arguments from a line entered by the user. */
public class Parser {
    private Parser() {
        // Utility class.
    }

    /**
     * Splits a full command into its command word and trimmed arguments.
     *
     * @param command the complete line entered by the user
     * @return an array containing the command word followed by its arguments
     */
    public static String[] parse(String command) {
        String[] splitCommand = command.split(" ", 2);
        String commandWord = splitCommand[0];
        String arguments = splitCommand.length > 1 ? splitCommand[1].trim() : "";
        return new String[] {commandWord, arguments};
    }
}
