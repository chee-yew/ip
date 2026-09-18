package whimsybot;

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
}
