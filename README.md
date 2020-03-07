# AcoWorth

AcoWorth is a simple plugin that hooks into Player Shops and tracks individual item sales. A player can later check the worth of the item and it will get an approximate average sale price for that item.

This plugin is great for economy servers to easily allow players to compete in the economy.

Currently, ChestShop and QuickShop Reremake are the only player shops supported at this time. If you would like to request a different plugin to be supported [please make an issue in the github issue tracker](https://github.com/LittleBigBug/acoworth/issues) and I'll get to it when I can.

- [ChestShop-3](https://github.com/ChestShop-authors/ChestShop-3) [Spigot](https://www.spigotmc.org/resources/chestshop.51856/)
- [QuickShop Reremake](https://github.com/Ghost-chu/QuickShop-Reremake) [Spigot](https://www.spigotmc.org/resources/quickshop-reremake-1-15-ready-bees-bees-bee.62575/)

[Visit AcoWorth's Spigot page for commands and permissions](https://www.spigotmc.org/resources/acoworth.74173/)
[AcoWorth Plugin Release builds are uploaded here](https://github.com/LittleBigBug/acoworth/releases)

# Building

AcoWorth uses Maven for handling most dependencies automatically. 
Quickshop Reremake does not have a maven repository so you must add the jar to the local maven repository. Either use the Quickshop Reremake .jar in /lib or a version of your choice with this maven command:

```
mvn install:install-file -Dfile=lib/QuickShop-Reremake.3.0.8.1.jar -DgroupId=org.maxgamer \
    -DartifactId=quickshop -Dversion=3.0.8.1 -Dpackaging=jar
```

Replace the filepath after -Dfile to the path of your QuickShop Plugin Jar

Build the maven project with the package goal/lifecycle and the AcoWorth jar will be built in `target/`
