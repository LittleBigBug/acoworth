package net.yasfu.acoworth;

import net.yasfu.acoworth.ShopListeners.ChestshopListener;
import net.yasfu.acoworth.ShopListeners.QuickshopListener;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class AcoWorthPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        String version = getDescription().getVersion();
        Logger logger = getLogger();

        logger.info("AcoWorth Started - " + version);
        logger.info("Developed by LittleBigBug");
        logger.info("https://github.com/littlebigbug/acoworth");

        File f = new File(getDataFolder() + "/");

        if (!f.exists()) {
            f.mkdir();
        }

        this.saveDefaultConfig();

        Storage.logger = logger;
        Storage.fileCfg = getConfig();
        Storage.connect();

        this.getCommand("worth").setExecutor(new WorthCommand(this));
        this.getCommand("acoworth").setExecutor(new AcoWorthCommand(this));

        Server srv = getServer();
        PluginManager plManager = srv.getPluginManager();

        plManager.registerEvents(new ChestshopListener(this), this);
        plManager.registerEvents(new QuickshopListener(this), this);
    }

    @Override
    public void onDisable() {
        String version = getDescription().getVersion();
        Logger logger = getLogger();

        logger.info("Stopping AcoWorth - " + version);

        Storage.logger = logger;
        Storage.disconnect();

        HandlerList.unregisterAll(this);
    }

}
