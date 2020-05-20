package net.yasfu.acoworth.ShopListeners;

import de.epiceric.shopchest.shop.Shop;
import net.yasfu.acoworth.AcoWorthPlugin;
import net.yasfu.acoworth.Storage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import de.epiceric.shopchest.event.ShopBuySellEvent;

/*
ShopChest
https://github.com/EpicEricEE/ShopChest
https://www.spigotmc.org/resources/shopchest.11431/
 */

public class ShopChestListener implements Listener {

    AcoWorthPlugin plugin;

    public ShopChestListener(AcoWorthPlugin pl) {
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onShopChestSale(ShopBuySellEvent shopBuySellEvent) {
        Shop shop = shopBuySellEvent.getShop();
        Shop.ShopType shopType = shop.getShopType();

        if (shopBuySellEvent.isCancelled() || shopType == Shop.ShopType.ADMIN) {
            return;
        }

        FileConfiguration cfg = plugin.getConfig();
        String type = cfg.getString("trackSaleTypes");

        if (type == null) {
            type = "BUY";
        }

        ShopBuySellEvent.Type eventType = shopBuySellEvent.getType();

        plugin.getLogger().info(eventType.toString());

        if ((type.equals("BUY") && eventType == ShopBuySellEvent.Type.SELL) || (type.equals("SELL") && eventType == ShopBuySellEvent.Type.BUY)) {
            return;
        }

        ItemStack item = shop.getItem().getItemStack();
        Material mat = item.getType();

        int amt = shopBuySellEvent.getNewAmount();
        double cost = shopBuySellEvent.getNewPrice();

        cost /= amt;

        Storage.addSale(mat, cost);
    }

}
