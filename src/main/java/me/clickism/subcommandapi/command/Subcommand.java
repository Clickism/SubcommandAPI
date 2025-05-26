package me.clickism.subcommandapi.command;

import me.clickism.subcommandapi.argument.Argument;
import me.clickism.subcommandapi.util.Named;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a subcommand.
 */
public abstract class Subcommand implements Named {
    /**
     * The label of the subcommand.
     */
    protected String label;

    /**
     * Whether the subcommand requires operator permissions.
     */
    protected boolean requiresOp;

    private final List<Argument<?>> arguments = new ArrayList<>();
    private final List<String> flags = new ArrayList<>();

    /**
     * Create a new subcommand with the given label.
     *
     * @param label      the label of the subcommand
     * @param requiresOp whether the subcommand requires operator permissions
     */
    public Subcommand(String label, boolean requiresOp) {
        this.label = label;
        this.requiresOp = requiresOp;
    }

    /**
     * Create a new subcommand with the given label.
     *
     * @param label the label of the subcommand
     */
    public Subcommand(String label) {
        this(label, false);
    }

    public Subcommand setRequiresOp() {
        this.requiresOp = true;
        return this;
    }

    public Subcommand setLabel(String label) {
        this.label = label;
        return this;
    }

    /**
     * Add an argument to the subcommand.
     *
     * @param argument the argument to add
     */
    protected void addArgument(Argument<?> argument) {
        arguments.add(argument);
    }

    /**
     * Add a flag to the subcommand.
     *
     * @param flag the flag to add
     */
    protected void addFlag(String flag) {
        flags.add(flag);
    }

    /**
     * Get the label of the subcommand.
     *
     * @return the label of the subcommand
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String getName() {
        return getLabel();
    }

    /**
     * Get the arguments of the subcommand.
     *
     * @return the arguments of the subcommand
     */
    public List<Argument<?>> getArguments() {
        return arguments;
    }

    /**
     * Get the flags of the subcommand.
     *
     * @return the flags of the subcommand
     */
    public List<String> getFlags() {
        return flags;
    }

    /**
     * Get the usage of the subcommand.
     *
     * @return the usage of the subcommand
     */
    public String getUsage() {
        String arguments = getArguments().stream()
                .map(Argument::getHint)
                .collect(Collectors.joining(" "));
        String flags = getFlags().stream()
                .map(flag -> "(--" + flag + ")")
                .collect(Collectors.joining(" "));
        return arguments + " " + flags;
    }

    /**
     * Get the tab completion for the subcommand.
     *
     * @param index  the current argument index of the cursor
     * @param sender the sender of the command
     * @param args   the (relevant) arguments of the command
     * @return the tab completion for the subcommand
     */
    public List<String> getTabCompletion(int index, CommandSender sender, String[] args) {
        List<Argument<?>> arguments = getArguments();
        String arg = args[index];
        if (index >= arguments.size() || (arg.startsWith("--") && !flags.isEmpty())) {
            return flags.stream()
                    .map(flag -> "--" + flag)
                    .collect(Collectors.toList());
        }
        return arguments.get(index).getTabCompletion(sender, arg);
    }

    /**
     * Execute the subcommand with the given arguments if the sender has the required permissions.
     * <p>
     * Only relevant arguments are passed.
     *
     * @param trace      the current trace of subcommands
     * @param sender     sender of the command
     * @param argHandler arguments
     * @return the result of the command
     */
    public CommandResult executeIfAllowed(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) {
        if (!canExecute(sender)) {
            return CommandResult.failureWithUsage("You can't run this command.");
        }
        return execute(trace, sender, argHandler);
    }

    /**
     * Execute the subcommand with the given arguments assuming the sender has the required permissions to
     * perform this command.
     * <p>
     * Only relevant arguments are passed.
     *
     * @param trace      the current trace of subcommands
     * @param sender     sender of the command
     * @param argHandler arguments
     * @return the result of the command
     */
    protected abstract CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException;

    /**
     * Check if the sender has permissions to execute the command.
     *
     * @param commandSender sender of the command
     * @return true if the sender has permissions to perform the command
     */
    public boolean canExecute(CommandSender commandSender) {
        return !requiresOp || commandSender.isOp();
    }
}
