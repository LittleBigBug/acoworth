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
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.DARK_AQUA + "AcoWorth v" + plugin.getDescription().getVersion() + " has been reloaded.");
            return true;
        }

        return true;
    }

}
