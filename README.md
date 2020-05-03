# AcoWorth

AcoWorth is a simple but useful stats plugin for economy spigot-based servers that hook into Player Shops and tracks individual item sales. A player can later check the worth of the item and it will get an approximate average sale price for that item.

This plugin is great for economy servers to easily allow players to compete in the economy.

Supports SQLite and MySQL / MariaDB. (see the config)

Currently, ChestShop and QuickShop Reremake are the only player shops supported at this time. If you would like to request a different plugin to be supported [please make an issue in the github issue tracker](https://github.com/LittleBigBug/acoworth/issues) and I'll get to it when I can.

- [ChestShop-3](https://github.com/ChestShop-authors/ChestShop-3) | [[Spigot]](https://www.spigotmc.org/resources/chestshop.51856/)
- [QuickShop Reremake](https://github.com/Ghost-chu/QuickShop-Reremake) | [[Spigot]](https://www.spigotmc.org/resources/quickshop-reremake-1-15-ready-bees-bees-bee.62575/)
- [QuantumShop (PREMIUM) [Spigot]](https://www.spigotmc.org/resources/quantumshop-1-13-1-15.50696/) 
- [AuctionHouse [Spigot]](https://www.spigotmc.org/resources/auctionhouse.61836/)
- [CrazyAuctions [Spigot]](https://www.spigotmc.org/resources/crazy-auctions.25219/)

## Helpful Links

- [Visit AcoWorth's Spigot page for commands and permissions](https://www.spigotmc.org/resources/acoworth.74173/)
- [AcoWorth Plugin Release builds are uploaded here](https://github.com/LittleBigBug/acoworth/releases)

# Building

AcoWorth uses Maven for handling most dependencies automatically. I use Jetbrains Intellij but any IDE that supports maven projects should work just as well.

Quickshop Reremake does not have a maven repository so you must add the jar to the local maven repository. Either use the Quickshop Reremake .jar in /lib or a version of your choice with this maven command:

```
mvn install:install-file -Dfile=lib/QuickShop-Reremake.3.0.8.1.jar -DgroupId=org.maxgamer \
    -DartifactId=quickshop -Dversion=3.0.8.1 -Dpackaging=jar
```

To build with QuantumShop you need to check the new build-with-quantum-shop profile in maven. You also need a copy of the plugin's jar in order to build, since the plugin is premium the jar is not allowed to be distributed on this repository. Perform the same maven command for Quickshop for Quantumshop to add it to the local maven repository as well. Quantum shop's group name is `su.nightexpress`. The oldest version you can build against is v3.6.8. You do have to build with FogusCore.

```
mvn install:install-file -Dfile=lib/QuantumShop.jar -DgroupId=su.nightexpress \
    -DartifactId=quantumshop -Dversion=3.6.8 -Dpackaging=jar

mvn install:install-file -Dfile=lib/FogusCore.jar -DgroupId=su.fogus \
    -DartifactId=core -Dversion=1.9.0 -Dpackaging=jar
```

Replace the filepath after -Dfile to the path of your QuickShop Plugin Jar

The same applies for AuctionHouse:

```
mvn install:install-file -Dfile=lib/AuctionHouse-2.0.8.jar -DgroupId=com.spawnchunk \
    -DartifactId=auctionhouse -Dversion=2.0.8 -Dpackaging=jar
```

Currently there is a loose implementation to support SnowGear's Premium Shop plugin (see [#2](https://github.com/LittleBigBug/acoworth/issues/2)). Obviously due to this plugin being premium I cannot include a jar in source, and not everyone who wants to compile has access to the plugin.

AcoWorth's maven project contains a profile called `build-with-snowgears-shop` and will not try to add snowgears as a dependency and will not load or compile the listener class. Sometimes using maven build packaging fails without Snowgears shop and the build snowgears shop profile disabled, `net.yasfu.acoworth/ShopListeners/SnowgearsListener.java` will also have syntax errors in the IDE. First run the "compile" goal/lifecycle before the package goal/lifecycle like shown below. 

If that doesn't work, just delete the SnowgearsListener.java class and the references to it in AcoWorthPlugin.java

Build the maven project with the package goal/lifecycle and the AcoWorth jar will be built in `target/`

# Contributing

You are welcome to send pull requests! Just try to stick to the code style. You are welcome to fix bugs, clean code, or add features that are relevant and useful to the plugin (up for discussion in pull request!)
