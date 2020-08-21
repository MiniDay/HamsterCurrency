package cn.hamster3.currency.core;

import cn.hamster3.currency.HamsterCurrency;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class FileManager {
    private static boolean mainServer;
    private static FileConfiguration pluginConfig;

    public static void reload(HamsterCurrency plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        pluginConfig = plugin.getConfig();
        mainServer = pluginConfig.getBoolean("mysql.template");
    }

    public static FileConfiguration getPluginConfig() {
        return pluginConfig;
    }

    public static void setPluginConfig(FileConfiguration pluginConfig) {
        FileManager.pluginConfig = pluginConfig;
    }

    public static boolean isMainServer() {
        return mainServer;
    }
}
