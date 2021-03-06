package net.yasfu.acoworth.ShopListeners;

import net.yasfu.acoworth.Storage;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import net.yasfu.acoworth.AcoWorthPlugin;
import su.nightexpress.qshop.modules.list.chestshop.event.ChestShopBuyEvent;
import su.nightexpress.qshop.modules.list.chestshop.event.ChestShopSellEvent;
import su.nightexpress.qshop.modules.list.chestshop.object.ShopChest;
import su.nightexpress.qshop.modules.list.gui.object.ShopProduct;
import su.nightexpress.qshop.modules.list.gui.object.PreparedProduct;
import su.nightexpress.qshop.modules.list.gui.event.GUIShopBuyItemEvent;
import su.nightexpress.qshop.modules.list.gui.event.GUIShopSellItemEvent;

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

        ShopChest shop = buyEvent.getShop();
        double price = shop.getBuyPrice(false);
        ItemStack stack = shop.getProductWithAmount();

        addQuantumSale(stack, price);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestShopSell(ChestShopSellEvent sellEvent) {
        FileConfiguration cfg = plugin.getConfig();
        String type = cfg.getString("trackSaleTypes");

        if (type == null || (!type.equals("SELL") && !type.equals("BOTH"))) {
            return;
        }

        ShopChest shop = sellEvent.getShop();
        double price = shop.getSellPrice(false);
        ItemStack stack = shop.getProductWithAmount();

        addQuantumSale(stack, price);
    }

    private void addQuantumSale(ItemStack stack, double price) {
        Material mat = stack.getType();
        plugin.getLogger().info(price + "");
        Storage.addSale(mat, price);
    }

    private void addQuantumSale(PreparedProduct item, double price) {
        ShopProduct shopProduct = item.getShopItem();
        ItemStack stack = shopProduct.getBuyItem();

        price /= item.getAmount();

        addQuantumSale(stack, price);
    }

}