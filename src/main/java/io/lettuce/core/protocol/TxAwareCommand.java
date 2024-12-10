package io.lettuce.core.protocol;
public class TxAwareCommand<K, V, T> extends CommandWrapper<K, V, T> {

    public TxAwareCommand(RedisCommand<K, V, T> command) {
        super(command);
    }

    /**
     * Check if the given command is a DeferredAuthCommand.
     *
     * @param command Redis command
     * @return true if the command is a DeferredAuthCommand, false otherwise
     */
    public static boolean isTxAwareCommand(RedisCommand<?, ?, ?> command) {

        if (command instanceof TxAwareCommand) {
            return true;
        }

        while (command instanceof CommandWrapper) {
            command = ((CommandWrapper<?, ?, ?>) command).getDelegate();

            if (command instanceof TxAwareCommand) {
                return true;
            }
        }

        return false;
    }
}
