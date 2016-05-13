package okkero.util;

/**
 * @param <ST> the sender type
 * @param <T>  the type of command
 */
public interface CommandHandler<ST, T extends Command> {

    void onCommand(ST sender, T command);

}
