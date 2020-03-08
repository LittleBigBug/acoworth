package net.yasfu.acoworth;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.yasfu.acoworth.ShopListeners.ChestshopListener;
import net.yasfu.acoworth.ShopListeners.QuickshopListener;

public class AcoWorthPlugin extends JavaPlugin {

    public static AcoWorthPlugin singleton;

    private static final boolean buildOptionalSnowgears = isSnowgearsPresent();
    private static final boolean buildOptionalQuantum = isQuantumPresent();
    private static boolean isSnowgearsPresent() {
        try {
            Class.forName("com.snowgears.shop.Shop");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    private static boolean isQuantumPresent() {
        try {
            Class.forName("su.nightexpress.quantumshop.QuantumShop");
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

        // TODO: this is gross

        boolean chestShopEnabled = plManager.isPluginEnabled("ChestShop");
        boolean quickShopEnabled = plManager.isPluginEnabled("QuickShop");

        if (chestShopEnabled) {
            plManager.registerEvents(new ChestshopListener(this), this);
            logger.info("ChestShop was found! Using ChestShop.");
        }

        if (quickShopEnabled) {
            plManager.registerEvents(new QuickshopListener(this), this);
            logger.info("QuickShop was found! Using QuickShop.");
        }

        if (buildOptionalSnowgears) {
            boolean gearsShopEnabled = plManager.isPluginEnabled("Shop");
            if (gearsShopEnabled) {
                plManager.registerEvents(new net.yasfu.acoworth.ShopListeners.SnowgearsListener(this), this);
                logger.info("Snowgears' Shop was found! Using Shop.");
            }
        }

        if (buildOptionalQuantum) {
            boolean quantShopEnabled = plManager.isPluginEnabled("QuantumShop");
            if (quantShopEnabled) {
                plManager.registerEvents(new net.yasfu.acoworth.ShopListeners.QuantumshopListener(this), this);
                logger.info("Quantumshop was found! Using Quantumshop. (Special developer build, chestshops and quantumshop do not work");
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
