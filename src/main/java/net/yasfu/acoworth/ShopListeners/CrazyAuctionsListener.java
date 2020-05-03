package net.yasfu.acoworth.ShopListeners;

import net.yasfu.acoworth.AcoWorthPlugin;
import net.yasfu.acoworth.Storage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import me.badbones69.crazyauctions.api.events.AuctionBuyEvent;

public class CrazyAuctionsListener implements Listener {

    AcoWorthPlugin plugin;

    public CrazyAuctionsListener(AcoWorthPlugin pl) {
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCrazyAuctionBuy(AuctionBuyEvent auctionBuyEvent) {
        FileConfiguration cfg = plugin.getConfig();
        boolean enabled = cfg.getBoolean("enableAuction");
        if (!enabled) { return; }

        ItemStack item = auctionBuyEvent.getItem();
        Material mat = item.getType();

        int itemCount = item.getAmount();

        long cost = auctionBuyEvent.getPrice();
        cost /= itemCount;

        Storage.addSale(mat, cost);
    }

}
