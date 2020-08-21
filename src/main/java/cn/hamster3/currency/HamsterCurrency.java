package cn.hamster3.currency;

import cn.hamster3.api.utils.LogUtils;
import cn.hamster3.currency.api.CurrencyAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class HamsterCurrency extends JavaPlugin {
    private static LogUtils logUtils;
    private static CurrencyAPI api;

    public static LogUtils getLogUtils() {
        return logUtils;
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        reloadConfig();
        logUtils = new LogUtils(this);
        if (getConfig().getBoolean("useBC")) {

        }
    }

    @Override
    public void onEnable() {
        logUtils.info("插件已启动!");
    }

    @Override
    public void onDisable() {
        logUtils.info("插件已关闭!");
    }
}
