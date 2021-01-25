package cn.hamster3.currency.core;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.currency.HamsterCurrency;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class FileManager {
    private static boolean useBC;
    private static boolean mainServer;
    private static boolean vaultHook;
    private static String vaultCurrencyType;
    private static FileConfiguration pluginConfig;

    public static void reload(HamsterCurrency plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        pluginConfig = plugin.getConfig();
        useBC = pluginConfig.getBoolean("useBC", false);
        mainServer = pluginConfig.getBoolean("datasource.template");
        setPluginConfig(pluginConfig);
    }

    public static FileConfiguration getPluginConfig() {
        return pluginConfig;
    }

    @SuppressWarnings("ConstantConditions")
    public static void setPluginConfig(FileConfiguration pluginConfig) {
        FileManager.pluginConfig = pluginConfig;

        vaultHook = pluginConfig.getBoolean("vault.hook");
        vaultCurrencyType = pluginConfig.getString("vault.type");

        ConfigurationSection messagesConfig = pluginConfig.getConfigurationSection("messages");
        for (String key : messagesConfig.getKeys(false)) {
            try {
                Message.valueOf(key).setMessage(HamsterAPI.replaceColorCode(messagesConfig.getString(key)));
            } catch (IllegalArgumentException e) {
                HamsterCurrency.getLogUtils().warning("初始化消息设置 %s 时发生了一个异常: ", key);
                e.printStackTrace();
            }
        }
    }

    public static boolean isUseBC() {
        return useBC;
    }

    public static boolean isMainServer() {
        return mainServer;
    }

    public static boolean isVaultHook() {
        return vaultHook;
    }

    public static String getVaultCurrencyType() {
        return vaultCurrencyType;
    }
}
