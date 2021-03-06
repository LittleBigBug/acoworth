package net.yasfu.acoworth.ShopListeners;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import net.yasfu.acoworth.Storage;
import net.yasfu.acoworth.AcoWorthPlugin;
import com.Acrobot.ChestShop.Events.TransactionEvent;

import static com.Acrobot.Breeze.Utils.InventoryUtil.mergeSimilarStacks;

/*
Chestshop 3
https://www.spigotmc.org/resources/chestshop.51856/
 */

public class ChestshopListener implements Listener {

    AcoWorthPlugin plugin;

    public ChestshopListener(AcoWorthPlugin pl) {
        plugin = pl;
    }

    @EventHandler (priority = EventPriority.MONITOR)
    private void onChestShopSale(TransactionEvent trEvent) {
        if (trEvent.isCancelled()) {
            return;
        }

        FileConfiguration cfg = plugin.getConfig();
        String type = cfg.getString("trackSaleTypes");

        if (type == null) {
            type = "BUY";
        }

        switch (type) {
            case "BUY":
                if (trEvent.getTransactionType() != TransactionEvent.TransactionType.BUY) {
                    return;
                }
                break;
            case "SELL":
                if (trEvent.getTransactionType() != TransactionEvent.TransactionType.SELL) {
                    return;
                }
                break;
        }

        ItemStack[] stock = trEvent.getStock();
        ItemStack item = stock[0];
        Material mat = item.getType();

        int itemCount = 0;

        for (ItemStack i : mergeSimilarStacks(stock)) {
            itemCount += i.getAmount();
        }

        double cost = trEvent.getExactPrice().doubleValue();
        cost /= itemCount;

        Storage.addSale(mat, cost);
    }

}
