package whimsybot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests user-facing command responses from Whimsy Bot. */
class WhimsyBotTest {
    @Test
    void helpCommand_returnsCommandGuidance() {
        WhimsyBot bot = new WhimsyBot();

        String response = bot.getResponse("help");

        assertTrue(response.contains("Here is how to use Whimsy Bot:"));
        assertTrue(response.contains("  todo DESCRIPTION - add a task without a date"));
        assertTrue(response.contains("  bye - exit Whimsy Bot"));
    }

    @Test
    void listCommand_withUnexpectedArguments_returnsHelpfulError() {
        WhimsyBot bot = new WhimsyBot();

        assertEquals("OOPS!!! The list command does not accept any arguments.",
                bot.getResponse("list now"));
    }

    @Test
    void commandWithIrregularWhitespace_isStillRecognized() {
        WhimsyBot bot = new WhimsyBot();

        assertTrue(bot.getResponse("  HELP  ").startsWith("Here is how to use Whimsy Bot:"));
    }

    @Test
    void deadlineCommand_withImpossibleIsoDate_returnsHelpfulError() {
        WhimsyBot bot = new WhimsyBot();

        assertEquals("OOPS!!! The deadline must be a valid date, such as 2026-09-20.",
                bot.getResponse("deadline date validation test /by 2026-02-30"));
    }

    @Test
    void eventCommand_withEndBeforeStart_returnsHelpfulError() {
        WhimsyBot bot = new WhimsyBot();

        assertEquals("OOPS!!! An event's start date must be before its end date.",
                bot.getResponse("event date order test /from 2026-09-21 /to 2026-09-20"));
    }

    @Test
    void todoCommand_withDuplicateTask_rejectsSecondTask() {
        WhimsyBot bot = new WhimsyBot();
        String command = "todo duplicate validation test";

        bot.getResponse(command);

        assertEquals("OOPS!!! This task is already in your list. Please add a different task.",
                bot.getResponse(command));
    }
}
