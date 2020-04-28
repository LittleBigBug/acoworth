package net.yasfu.acoworth.ShopListeners;

import net.yasfu.acoworth.Storage;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import net.yasfu.acoworth.AcoWorthPlugin;
import su.nightexpress.quantumshop.modules.list.chestshop.events.ChestShopBuyEvent;
import su.nightexpress.quantumshop.modules.list.chestshop.events.ChestShopSellEvent;
import su.nightexpress.quantumshop.modules.list.gui.objects.ShopProduct;
import su.nightexpress.quantumshop.modules.list.gui.objects.PreparedProduct;
import su.nightexpress.quantumshop.modules.list.gui.events.GUIShopBuyItemEvent;
import su.nightexpress.quantumshop.modules.list.gui.events.GUIShopSellItemEvent;

/*
QuantumShop
https://www.spigotmc.org/resources/quantumshop-1-13-1-15.50696/
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
        boolean cont = cfg.getBoolean("quantumShopAdminShop");

        if (!cont) { return; }

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
        boolean cont = cfg.getBoolean("quantumShopAdminShop");

        if (!cont) { return; }

        if (type == null || (!type.equals("SELL") && !type.equals("BOTH"))) {
            return;
        }

        addQuantumSale(sellEvent.getItem(), sellEvent.getTotalPrice());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestShopBuy(ChestShopBuyEvent buyEvent) {
        FileConfiguration cfg = plugin.getConfig();
        String type = cfg.getString("trackSaleTypes");

        if (type == null || !type.equals("BUY") && !type.equals("BOTH")) {
            return;
        }

        addQuantumSale(buyEvent.getShop().getProductWithAmount(), buyEvent.getPrice());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestShopSell(ChestShopSellEvent sellEvent) {
        FileConfiguration cfg = plugin.getConfig();
        String type = cfg.getString("trackSaleTypes");

        if (type == null || (!type.equals("SELL") && !type.equals("BOTH"))) {
            return;
        }

        addQuantumSale(sellEvent.getShop().getProductWithAmount(), sellEvent.getPrice());
    }

    private void addQuantumSale(ItemStack stack, double totalPrice) {
        Material mat = stack.getType();

        double price = totalPrice;
        price /= stack.getAmount();

        plugin.getLogger().log(" a "+ stack.getAmount());

        Storage.addSale(mat, price);
    }

    private void addQuantumSale(PreparedProduct item, double totalPrice) {
        ShopProduct shopProduct = item.getShopItem();
        ItemStack stack = shopProduct.getBuyItem();
        stack.setAmount(item.getAmount());

        addQuantumSale(stack, totalPrice);
    }

}