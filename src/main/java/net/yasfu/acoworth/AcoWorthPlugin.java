package net.yasfu.acoworth;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.Acrobot.ChestShop.ChestShop;
import org.maxgamer.quickshop.QuickShop;
import com.spawnchunk.auctionhouse.AuctionHouse;
import net.yasfu.acoworth.ShopListeners.ChestshopListener;
import net.yasfu.acoworth.ShopListeners.QuickshopListener;
import net.yasfu.acoworth.ShopListeners.AuctionHouseListener;

public class AcoWorthPlugin extends JavaPlugin {

    public static AcoWorthPlugin singleton;

    private static final boolean buildOptionalSnowgears = isSnowgearsPresent();
    private static boolean isSnowgearsPresent() {
        try {
            Class.forName("com.snowgears.shop.Shop");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void onEnable() {
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

        this.getCommand("worth").setExecutor(new WorthCommand(this));
        this.getCommand("acoworth").setExecutor(new AcoWorthCommand(this));

        Server srv = getServer();
        PluginManager plManager = srv.getPluginManager();

        Plugin chestShop = plManager.getPlugin("ChestShop");
        Plugin quickShop = plManager.getPlugin("QuickShop");
        Plugin auctionHouse = plManager.getPlugin("AuctionHouse");

        if (chestShop instanceof ChestShop) {
            plManager.registerEvents(new ChestshopListener(this), this);
            logger.info("ChestShop was found! Using ChestShop.");
        }

        if (quickShop instanceof QuickShop) {
            plManager.registerEvents(new QuickshopListener(this), this);
            logger.info("QuickShop was found! Using QuickShop.");
        }

        if (auctionHouse instanceof AuctionHouse) {
            plManager.registerEvents(new AuctionHouseListener(this), this);
            logger.info("AuctionHouse was found! Using AuctionHouse.");
        }

        if (!buildOptionalSnowgears) {
            Plugin gearsShop = plManager.getPlugin("Shop");
            if (gearsShop instanceof com.snowgears.shop.Shop) {
                plManager.registerEvents(new net.yasfu.acoworth.ShopListeners.SnowgearsListener(this), this);
                logger.info("Snowgears' Shop was found! Using Shop.");
            }
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
