package net.yasfu.acoworth;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.snowgears.shop.Shop;
import com.Acrobot.ChestShop.ChestShop;
import de.epiceric.shopchest.ShopChest;
import org.maxgamer.quickshop.QuickShop;
import me.badbones69.crazyauctions.Main;
import su.nightexpress.qshop.QShop;
import com.spawnchunk.auctionhouse.AuctionHouse;
import net.yasfu.acoworth.ShopListeners.ChestshopListener;
import net.yasfu.acoworth.ShopListeners.ShopChestListener;
import net.yasfu.acoworth.ShopListeners.QuickshopListener;
import net.yasfu.acoworth.ShopListeners.AuctionHouseListener;

public class AcoWorthPlugin extends JavaPlugin {

    public static AcoWorthPlugin singleton;

    public static Metrics metrics;

    private static final int pluginId = 10573;

    @Override
    public void onEnable() {
        new Metrics(this, pluginId);

        String version = getDescription().getVersion();
        Logger logger = getLogger();

        logger.info("AcoWorth Started - " + version);
        logger.info("Developed by LittleBigBug");
        logger.info("https://github.com/littlebigbug/acoworth");

        File f = new File(getDataFolder() + "/");
        if (!f.exists()) { f.mkdir(); }
        this.saveDefaultConfig();

        singleton = this;
        Storage.connect();

        PluginCommand worthCmd = this.getCommand("worth");
        PluginCommand acoWorthCmd = this.getCommand("acoworth");

        if (worthCmd == null || acoWorthCmd == null) {
            logger.log(Level.SEVERE, "AcoWorth worth or acoworth command missing (??)");
            return;
        }

        worthCmd.setExecutor(new WorthCommand(this));
        acoWorthCmd.setExecutor(new AcoWorthCommand(this));

        Server srv = getServer();
        PluginManager plManager = srv.getPluginManager();

        Plugin chestShop = plManager.getPlugin("ChestShop");
        Plugin shopChest = plManager.getPlugin("ShopChest");
        Plugin quickShop = plManager.getPlugin("QuickShop");
        Plugin auctionHouse = plManager.getPlugin("AuctionHouse");

        if (chestShop instanceof ChestShop) {
            plManager.registerEvents(new ChestshopListener(this), this);
            logger.info("ChestShop was found! Using ChestShop.");
        }

        if (shopChest instanceof ShopChest) {
            plManager.registerEvents(new ShopChestListener(this), this);
            logger.info("ShopChest was found! Using ShopChest.");
        }

        if (quickShop instanceof QuickShop) {
            plManager.registerEvents(new QuickshopListener(this), this);
            logger.info("QuickShop was found! Using QuickShop.");
        }

        if (auctionHouse instanceof AuctionHouse) {
            plManager.registerEvents(new AuctionHouseListener(this), this);
            logger.info("AuctionHouse was found! Using AuctionHouse.");
        }

        Plugin gearsShop = plManager.getPlugin("Shop");
        if (gearsShop instanceof Shop) {
            plManager.registerEvents(new net.yasfu.acoworth.ShopListeners.SnowgearsListener(this), this);
            logger.info("Snowgears' Shop was found! Using Shop.");
        }

        Plugin quantumShop = plManager.getPlugin("QuantumShop");
        if (quantumShop instanceof QShop) {
            plManager.registerEvents(new net.yasfu.acoworth.ShopListeners.QuantumshopListener(this), this);
            logger.info("QuantumShop was found! Using QuantumShop.");
        }

        Plugin crazyAuctions = plManager.getPlugin("CrazyAuctions");
        if (crazyAuctions instanceof Main) {
            plManager.registerEvents(new net.yasfu.acoworth.ShopListeners.CrazyAuctionsListener(this), this);
            logger.info("CrazyAuctions was found! Using CrazyAuctions.");
        }
    }

    @Override
    public void onDisable() {
        String version = getDescription().getVersion();
        Logger logger = getLogger();

        logger.info("Stopping AcoWorth - " + version);
        Storage.disconnect();
        HandlerList.unregisterAll(this);
    }

}
