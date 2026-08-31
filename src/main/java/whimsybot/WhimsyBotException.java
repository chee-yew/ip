package whimsybot;

/**
 * Signals that Whimsy Bot could not understand or carry out a user command,
 * e.g. an unrecognised command, a missing task description, or an invalid
 * task number.
 */
public class WhimsyBotException extends Exception {

    /** Creates an exception with the supplied user-facing message. */
    public WhimsyBotException(String message) {
        super(message);
    }
}
