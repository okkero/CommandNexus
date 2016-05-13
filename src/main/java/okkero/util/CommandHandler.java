package okkero.util;

/**
 * @param <S> the sender type
 * @param <T> the type of command
 */
public interface CommandHandler<S, T extends Command> {

    void onCommand(S sender, T command);

}
