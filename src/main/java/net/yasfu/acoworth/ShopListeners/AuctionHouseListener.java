package net.yasfu.acoworth.ShopListeners;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import net.yasfu.acoworth.Storage;
import net.yasfu.acoworth.AcoWorthPlugin;
import com.spawnchunk.auctionhouse.events.AuctionItemEvent;

public class AuctionHouseListener implements Listener {

    AcoWorthPlugin plugin;

    public AuctionHouseListener(AcoWorthPlugin pl) {
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onAuctionSale(AuctionItemEvent aucEvent) {
        FileConfiguration cfg = plugin.getConfig();
        boolean enabled = cfg.getBoolean("enableAuction");
        if (!enabled) { return; }

        ItemStack item = aucEvent.getItem();
        Material mat = item.getType();

        int itemCount = item.getAmount();

        double cost = aucEvent.getPrice();
        cost /= itemCount;

        Storage.addSale(mat, cost);
    }

}
