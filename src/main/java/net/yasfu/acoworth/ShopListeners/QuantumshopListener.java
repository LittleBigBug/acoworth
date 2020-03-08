package net.yasfu.acoworth.ShopListeners;

import net.yasfu.acoworth.Storage;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import net.yasfu.acoworth.AcoWorthPlugin;
import su.nightexpress.quantumshop.modules.list.gui.objects.ShopProduct;
import su.nightexpress.quantumshop.modules.list.gui.objects.PreparedProduct;
import su.nightexpress.quantumshop.modules.list.gui.events.GUIShopBuyItemEvent;
import su.nightexpress.quantumshop.modules.list.gui.events.GUIShopSellItemEvent;

/*
QuantumShop
https://www.spigotmc.org/resources/quantumshop-1-13-1-15.50696/

Premium plugin. If you don't own a copy of this plugin you need to build without it.
Build with Maven WITHOUT the build-with-quantum-shop profile checked.
If you want to build with quantumshop, make sure to install the jar to a local repo
https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html

mvn install:install-file -Dfile=lib/QuantumShop.jar -DgroupId=su.nightexpress \
    -DartifactId=quantumshop -Dversion=3.6.7 -Dpackaging=jar
 */

public class QuantumshopListener implements Listener {

    AcoWorthPlugin plugin;

    public QuantumshopListener(AcoWorthPlugin pl) {
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGUIShopBuy(GUIShopBuyItemEvent buyEvent) {
        if (buyEvent.isCancelled()) {
            return;
        }

        FileConfiguration cfg = plugin.getConfig();
        String type = cfg.getString("trackSaleTypes");

        if (type == null || !type.equals("BUY") && !type.equals("BOTH")) {
            return;
        }

        addQuantumSale(buyEvent.getItem(), buyEvent.getTotalPrice());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGUIShopSell(GUIShopSellItemEvent sellEvent) {
        if (sellEvent.isCancelled()) {
            return;
        }

        FileConfiguration cfg = plugin.getConfig();
        String type = cfg.getString("trackSaleTypes");

        if (type == null || (!type.equals("SELL") && !type.equals("BOTH"))) {
            return;
        }

        addQuantumSale(sellEvent.getItem(), sellEvent.getTotalPrice());
    }

    private void addQuantumSale(PreparedProduct item, double totalPrice) {
        ShopProduct shopProduct = item.getShopItem();
        ItemStack stack = shopProduct.getBuyItem();
        Material mat = stack.getType();

        double price = totalPrice;
        price /= item.getAmount();

        Storage.addSale(mat, price);
    }

}