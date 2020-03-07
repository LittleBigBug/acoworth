package net.yasfu.acoworth;

import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class WorthCommand implements CommandExecutor {

    private AcoWorthPlugin plugin;

    public WorthCommand(AcoWorthPlugin pl) {
        plugin = pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player) && args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + s + " [amount=1] [item id]");
            return true;
        }

        Material checkMat = null;
        int amount = 1;

        if (args.length > 0 && args[0] != null) {
            String matArg = args[0];

            if (args.length > 1 && args[1] != null) {
                matArg = args[1];

                try {
                    amount = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    amount = 1;
                }
            }

            Material mat = Material.matchMaterial(matArg);

            if (mat != null) {
                checkMat = mat;
            } else {
                try {
                    amount = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "The id '" + matArg + "' is not a valid minecraft material.");
                    return true;
                }
            }
        }

        if (checkMat == null) {
            Player ply = (Player) sender;
            ItemStack item = ply.getInventory().getItemInMainHand();

            checkMat = item.getType();

            if (checkMat == Material.AIR) {
                ply.sendMessage(ChatColor.RED + "Air doesn't have any worth!");
                return true;
            }
        }

        double worth = Storage.getWorth(checkMat);

        // TODO clean this

        switch ((int) worth) {
            case -2:
                sender.sendMessage(ChatColor.RED + "There was a database error when trying to get the worth for this item. Check console for more details.");
                return true;
            case -1:
                sender.sendMessage(ChatColor.RED + "There is no value history for this item.");
                return true;
        }

        worth *= amount;

        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
        String formatWorth = decimalFormat.format(worth);

        sender.sendMessage(ChatColor.DARK_AQUA + Integer.toString(amount) + " " + ChatColor.AQUA + checkMat.name() + ChatColor.DARK_AQUA + " on average sells for " + ChatColor.AQUA + "$" + formatWorth);

        return true;
    }

}
