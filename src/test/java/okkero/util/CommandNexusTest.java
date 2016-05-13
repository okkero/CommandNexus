package okkero.util;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class CommandNexusTest {

    private MockSender sender;
    private CommandNexus<MockSender> nexus;

    @Before
    public void setup() {
        sender = new MockSender();
        nexus = new CommandNexus<>(MockSender::receive);
    }

    @Test
    public void testReceiveCommand() throws Exception {
        String json = readJSONFromResource("mockcommand.json");

        String[] message = {null};
        nexus.handleCommand("mock", MockCommand.class,
                (sender, command) -> message[0] = command.message);
        nexus.onCommand(sender, json);

        assertEquals("mock123", message[0]);
    }

    @Test
    public void testSendCommand() throws Exception {
        assertNull(sender.lastReceivedMessage);

        MockCommand cmd = new MockCommand();
        cmd.message = "abc";
        nexus.sendCommand(sender, cmd);

        assertNotNull(sender.lastReceivedMessage);
    }

    private String readJSONFromResource(String resource) throws Exception {
        Path path = Paths.get(getClass().getResource(resource).toURI());
        return new String(Files.readAllBytes(path));
    }

}
