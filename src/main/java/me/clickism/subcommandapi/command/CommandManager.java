package me.clickism.subcommandapi.command;

import me.clickism.subcommandapi.util.NamedCollection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Manages commands and their execution.
 */
public class CommandManager implements CommandExecutor {
    private final TabCompleter tabCompleter = new CommandTabCompleter(this);
    private final NamedCollection<Subcommand> commands = new NamedCollection<>(new ArrayList<>());

    /**
     * Creates a new command manager.
     */
    public CommandManager() {
    }

    /**
     * Registers a root command.
     * <p>If the root command is not registered in plugin.yml, will do nothing.</p>
     *
     * @param subcommand subcommand to register as a root command
     */
    public void registerCommand(Subcommand subcommand) {
        commands.add(subcommand);
        String label = subcommand.getLabel();
        PluginCommand pluginCommand = Bukkit.getPluginCommand(label);
        if (pluginCommand == null) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "This root subcommand's label is not registered: '" + label + "'. Check plugin.yml.");
            return;
        }
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(tabCompleter);
    }

    /**
     * Get the named collection of root subcommands.
     *
     * @return named collection of root subcommands
     */
    public NamedCollection<Subcommand> getCommands() {
        return commands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        Subcommand subcommand = commands.get(label);
        if (subcommand == null) return false;
        CommandStack trace = new CommandStack();
        try {
            ArgumentHandler argHandler = new ArgumentHandler(sender, subcommand, args);
            trace.push(subcommand);
            CommandResult result = subcommand.executeIfAllowed(trace, sender, argHandler);
            handleCommandResult(trace, sender, result);
        } catch (CommandException exception) {
            handleCommandResult(trace, sender, CommandResult.failure(exception.getMessage()));
        } catch (Exception exception) {
            handleException(command, args, trace, sender, exception);
        }
        return true;
    }

    /**
     * Handles the result of a command execution.
     *
     * @param trace  command stack trace
     * @param sender sender of the command
     * @param result result of the command execution
     */
    protected void handleCommandResult(CommandStack trace, CommandSender sender, CommandResult result) {
        if (result.getMessage() == null) return;
        if (result.isFailure()) {
            sendMessage(sender, result.getType(), result.getMessage() + "\nUsage: " + trace.buildUsage());
            return;
        }
        sendMessage(sender, result.getType(), result.getMessage());
    }

    /**
     * Handles an exception that occurred during command execution.
     *
     * @param command   command that was executed
     * @param args      arguments passed to the command
     * @param trace     command stack trace
     * @param sender    sender of the command
     * @param exception exception that occurred
     */
    protected void handleException(Command command, String[] args, CommandStack trace, CommandSender sender, Exception exception) {
        sendMessage(sender, CommandResult.CommandResultType.FAILURE,
                "An error occurred while executing this command: &l" + exception.getMessage());
        String commandString = command.getLabel() + " " + String.join(" ", args);
        Bukkit.getLogger().log(Level.SEVERE,
                "An error occurred when " + sender.getName() + " tried executing command '/" + commandString +
                "': " + exception.getMessage(), exception);
    }

    /**
     * Sends a message to the sender with formatting based on the command result type.
     *
     * @param sender     sender to send the message to
     * @param resultType type of the message
     * @param message    message to send
     */
    protected void sendMessage(CommandSender sender, CommandResult.CommandResultType resultType, String message) {
        switch (resultType) {
            case SUCCESS -> sender.sendMessage(ChatColor.GREEN + message);
            case FAILURE -> sender.sendMessage(ChatColor.RED + message);
            case WARNING -> sender.sendMessage(ChatColor.YELLOW + message);
        }
    }
}
