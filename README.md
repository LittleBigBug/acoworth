# AcoWorth

AcoWorth is a simple but useful stats plugin for economy spigot-based servers that hook into Player Shops and tracks individual item sales. A player can later check the worth of the item and it will get an approximate average sale price for that item.

This plugin is great for economy servers to easily allow players to compete in the economy.

Supports SQLite and MySQL / MariaDB. (see the config)

If you would like to request a different plugin to be supported [please make an issue in the github issue tracker](https://github.com/LittleBigBug/acoworth/issues) and I'll get to it when I can.

Here are the current supported shop/player sales plugins:

- [ChestShop-3](https://github.com/ChestShop-authors/ChestShop-3) | [[Spigot]](https://www.spigotmc.org/resources/chestshop.51856/)
- [ShopChest](https://github.com/EpicEricEE/ShopChest) | [[Spigot]](https://www.spigotmc.org/resources/shopchest.11431/)
- [QuickShop Reremake](https://github.com/Ghost-chu/QuickShop-Reremake) | [[Spigot]](https://www.spigotmc.org/resources/quickshop-reremake-1-15-ready-bees-bees-bee.62575/)
- [QuantumShop (PREMIUM) [Spigot]](https://www.spigotmc.org/resources/quantumshop-1-13-1-15.50696/) 
- [AuctionHouse [Spigot]](https://www.spigotmc.org/resources/auctionhouse.61836/)
- [CrazyAuctions [Spigot]](https://www.spigotmc.org/resources/crazy-auctions.25219/)

## Helpful Links

- [Visit AcoWorth's Spigot page for commands and permissions](https://www.spigotmc.org/resources/acoworth.74173/)
- [AcoWorth Plugin Release builds are uploaded here](https://github.com/LittleBigBug/acoworth/releases)

# Building

AcoWorth uses Maven for handling most dependencies automatically. I use Jetbrains Intellij but any IDE that supports maven projects should work just as well.

Auction House does not have a maven repository so you must add the jar to the local maven repository. Either use the AuctionHouse .jar in /lib or a version of your choice with this maven command:

```
mvn install:install-file -Dfile=lib/AuctionHouse-2.0.8.jar -DgroupId=com.spawnchunk \
    -DartifactId=auctionhouse -Dversion=2.0.8 -Dpackaging=jar
```

To build with QuantumShop you need a copy of the plugin's jar in order to build, since the plugin is premium the jar is not allowed to be distributed on this repository. Perform the same maven command for AuctionHouse for Quantumshop to add it to the local maven repository as well. Quantum shop's group name is `su.nightexpress`. The oldest version you can build against is v3.6.8. You do have to build with FogusCore.

```
mvn install:install-file -Dfile=lib/QuantumShop.jar -DgroupId=su.nightexpress \
    -DartifactId=quantumshop -Dversion=3.7.2 -Dpackaging=jar

mvn install:install-file -Dfile=lib/FogusCore.jar -DgroupId=su.fogus \
    -DartifactId=core -Dversion=1.9.0 -Dpackaging=jar
```

Replace the filepath after -Dfile to the path of your Plugin Jar

Currently there is a loose implementation to support SnowGear's Premium Shop plugin (see [#2](https://github.com/LittleBigBug/acoworth/issues/2)). Obviously due to this plugin being premium I cannot include a jar in source, and not everyone who wants to compile has access to the plugin.

There are a couple of dependencies which have not been included due to the fact they are premium plugins. If you are building this yourself and you don't own these plugins, you may have to remove the listener class and the references to it in the main plugin class. After that it should build cleanly.

Build the maven project with the package goal/lifecycle and the AcoWorth jar will be built in `target/`

# Contributing

You are welcome to send pull requests! Just try to stick to the code style. You are welcome to fix bugs, clean code, or add features that are relevant and useful to the plugin (up for discussion in pull request!)
