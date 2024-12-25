package me.clickism.subcommandapi.argument;

import me.clickism.subcommandapi.command.CommandException;
import me.clickism.subcommandapi.util.Named;
import me.clickism.subcommandapi.util.NamedCollection;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents an argument that is selected from a collection of named options.
 * The argument is parsed by selecting the option with the given name using the NamedCollection.
 *
 * @param <T> type of the named options
 */
public class SelectionArgument<T extends Named> extends Argument<T> {
    private final Supplier<NamedCollection<T>> optionsSupplier;

    /**
     * Creates a new selection argument.
     *
     * @param name     name of the argument
     * @param required whether the argument is required
     * @param options  collection of named options
     */
    public SelectionArgument(String name, boolean required, NamedCollection<T> options) {
        this(name, required, () -> options);
    }

    /**
     * Creates a new selection argument.
     *
     * @param name            name of the argument
     * @param required        whether the argument is required
     * @param optionsSupplier supplier of the collection of named options
     */
    public SelectionArgument(String name, boolean required, Supplier<NamedCollection<T>> optionsSupplier) {
        super(name, required);
        this.optionsSupplier = optionsSupplier;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        return optionsSupplier.get().stream()
                .map(Named::getName)
                .toList();
    }

    @Override
    public T parse(CommandSender sender, String arg) throws CommandException {
        T option = optionsSupplier.get().get(arg);
        if (option == null) {
            throw new CommandException("Invalid " + getName() + ": &l" + arg);
        }
        return option;
    }
}
