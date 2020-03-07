package net.yasfu.acoworth.ShopListeners;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import net.yasfu.acoworth.Storage;
import net.yasfu.acoworth.AcoWorthPlugin;
import com.snowgears.shop.ShopType;
import com.snowgears.shop.AbstractShop;
import com.snowgears.shop.event.PlayerExchangeShopEvent;

/*
Shop by Snowgears
https://www.spigotmc.org/resources/shop-a-simple-intuitive-shop-plugin.9628/

Premium plugin. If you don't own a copy of this plugin you need to build without it.
Build with Maven WITHOUT the build-with-snowgears-shop profile checked.
If you want to build with snowgears, make sure to install the jar to a local repo
https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html

mvn install:install-file -Dfile=lib/Shop-SnowGears.jar -DgroupId=com.snowgears \
    -DartifactId=shop -Dversion=1.8.1 -Dpackaging=jar
 */

public class SnowgearsListener implements Listener {

    AcoWorthPlugin plugin;

    public SnowgearsListener(AcoWorthPlugin pl) {
        plugin = pl;
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onShopSale(PlayerExchangeShopEvent shopExchangeEvent) {
        if (shopExchangeEvent.isCancelled()) {
            return;
        }

        FileConfiguration cfg = plugin.getConfig();
        String type = cfg.getString("trackSaleTypes");

        AbstractShop shop = shopExchangeEvent.getShop();
        ShopType shopType = shop.getType();

        if (type == null) {
            type = "BUY";
        }

        switch (shopType) {
            case BUY:
                if (type.equals("SELL")) {
                    return;
                }
                break;
            case SELL:
                if (type.equals("BUY")) {
                    return;
                }
                break;
            case COMBO:
                // Does not look like this has been implemented yet.
                // Snowgears needs to add a way to find out what one of the combos happened in the event class.
                return;
            case BARTER:
                // Maybe in the future I can add something to get the value of the items traded and act like they were
                // a currency to buy one another, and store that. This would be a changeable setting
                return;
            case GAMBLE:
                return;
        }

        ItemStack item = shop.getItemStack();
        Material mat = item.getType();

        int amt = shop.getAmount();
        double cost = shop.getPrice();

        cost /= amt;

        Storage.addSale(mat, cost);
    }

}