package me.clickism.subcommandapi.argument;

/**
 * Represents an argument that is a long.
 */
public class LongArgument extends ValueArgument<Long> {
    /**
     * Create a new long argument with the given name and required status.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public LongArgument(String name, boolean required) {
        super(name, required, Long::parseLong);
    }
}
