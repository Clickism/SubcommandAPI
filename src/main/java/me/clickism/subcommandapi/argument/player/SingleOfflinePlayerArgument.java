package me.clickism.subcommandapi.argument.player;

import me.clickism.subcommandapi.argument.Argument;
import me.clickism.subcommandapi.command.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an argument that parses a single offline player.
 */
public class SingleOfflinePlayerArgument extends Argument<OfflinePlayer> {
    /**
     * Creates a new single offline player argument.
     *
     * @param name     the key of the argument
     * @param required whether the argument is required
     */
    public SingleOfflinePlayerArgument(String name, boolean required) {
        super(name, required);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        List<String> list = Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        list.add("@r");
        list.add("@p");
        list.add("@s");
        return list;
    }

    public OfflinePlayer parse(CommandSender sender, String arg) {
        if (arg.equalsIgnoreCase("@p") || arg.equalsIgnoreCase("@s")) {
            if (sender instanceof Player player) {
                return player;
            }
            throw new CommandException("Only players can use this selector.");
        }
        if (arg.equalsIgnoreCase("@r")) {
            OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
            if (offlinePlayers.length == 0) {
                throw new CommandException("No players online.");
            }
            return offlinePlayers[(int) (Math.random() * offlinePlayers.length)];
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
        return player;
    }
}
