package net.yasfu.acoworth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AcoWorthCommand implements CommandExecutor {

    private AcoWorthPlugin plugin;

    public AcoWorthCommand(AcoWorthPlugin pl) {
        plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Running AcoWorth v" + plugin.getDescription().getVersion() + "! The only subcommand is " + ChatColor.WHITE + " /aw reload " + ChatColor.DARK_AQUA + " for now");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            // reload cfg
            plugin.reloadConfig();

            // Redo connection
            Storage.disconnect();
            Storage.connect();

            sender.sendMessage(ChatColor.DARK_AQUA + "AcoWorth v" + plugin.getDescription().getVersion() + " has been reloaded.");
            return true;
        }

        return true;
    }

}
