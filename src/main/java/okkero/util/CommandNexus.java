package okkero.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @param <S> the sender type
 */
public class CommandNexus<S> {

    private final Map<String, Class<? extends Command>> nameToClass = new HashMap<>();
    private final Map<Class<? extends Command>, CommandHandler<S, ?>> classToHandler = new HashMap<>();

    private final Gson gson;
    private final BiConsumer<S, String> sendDelegate;

    public CommandNexus(BiConsumer<S, String> sendDelegate) {
        this(new Gson(), sendDelegate);
    }

    public CommandNexus(Gson gson, BiConsumer<S, String> sendDelegate) {
        this.gson = gson;
        this.sendDelegate = sendDelegate;
    }

    /**
     * Register a command handler to the nexus, for handling a specific type of command.
     *
     * @param commandName  the name of the command to handle
     * @param commandClass the class of the command to handle
     * @param handler      the handler responsible for handling the command
     * @param <T>          the type of command to handle
     */
    public <T extends Command> void handleCommand(String commandName, Class<T> commandClass, CommandHandler<S, T> handler) {
        //The following ensures that command names and command classes are appropriately associated
        //with handlers capable of handling commands of that type.
        nameToClass.put(commandName, commandClass);
        classToHandler.put(commandClass, handler);
    }

    /**
     * Handle a command sent from a session. This will delegate the command handling to registered command handlers.
     *
     * @param sender the sender of the command
     * @param json   the command that was sent, in JSON format
     */
    public void onCommand(S sender, String json) {
        Command command = parseCommand(json);
        CommandHandler handler = getCommandHandler(command.getClass());

        //This handler is guaranteed to be able to handle the command type at this point
        handler.onCommand(sender, command);
    }

    /**
     * Parse a command from JSON into a command object.
     * <p>
     * The JSON is required to have a property named "commandname" with the name of the command as value.
     *
     * @return the parsed command object
     */
    public Command parseCommand(String json) {
        JsonParser parser = new JsonParser();
        JsonElement elem = parser.parse(json);
        String commandName = elem.getAsJsonObject().get("commandname").getAsString();

        return gson.fromJson(elem, getCommandType(commandName));
    }

    /**
     * Get the class of the command type with the given name.
     *
     * @param commandName name of the command type
     * @return the class associated with the given name
     */
    private Class<? extends Command> getCommandType(String commandName) {
        return nameToClass.get(commandName);
    }

    /**
     * Get the handler responsible for handling commands of the specified name.
     *
     * @param commandName the name of the command to be handled
     * @return the CommandHandler object responsible for handling commands with the name
     */
    public CommandHandler<S, ?> getCommandHandler(String commandName) {
        Class<? extends Command> commandClass = getCommandType(commandName);
        return getCommandHandler(commandClass);
    }

    /**
     * Get the handler responsible for handling commands of the specified class.
     *
     * @param commandClass the class of the command to be handled
     * @param <T>          the type of command to be handled
     * @return the CommandHandler object responsible for handling commands of the type
     */
    public <T extends Command> CommandHandler<S, T> getCommandHandler(Class<T> commandClass) {
        //The command handler returned is guaranteed to be of the correct type (see #handleCommand)
        return (CommandHandler<S, T>) classToHandler.get(commandClass);
    }

    /**
     * Sends a command to a specified client.
     *
     * @param recipient the receiving client
     * @param command   the command to send
     * @throws IOException
     */
    public void sendCommand(S recipient, Command command) throws IOException {
        sendDelegate.accept(recipient, convertToJSON(command));
    }

    /**
     * Converts a command into a JSON formatted string.
     *
     * @param command the command to convert
     * @return a JSON formatted string
     */
    public String convertToJSON(Command command) {
        return gson.toJson(command);
    }

}
