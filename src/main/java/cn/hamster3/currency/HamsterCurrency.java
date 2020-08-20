package cn.hamster3.currency;

import cn.hamster3.api.utils.LogUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class HamsterCurrency extends JavaPlugin {
    private static LogUtils logUtils;

    public static LogUtils getLogUtils() {
        return logUtils;
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        reloadConfig();
        logUtils = new LogUtils(this);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
