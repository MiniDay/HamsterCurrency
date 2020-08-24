package cn.hamster3.currency;

import cn.hamster3.api.utils.LogUtils;
import cn.hamster3.currency.api.CurrencyAPI;
import cn.hamster3.currency.command.CurrencyCommand;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.SQLDataManager;
import cn.hamster3.currency.hook.PlaceholderHook;
import cn.hamster3.currency.listener.CurrencyListener;
import cn.hamster3.currency.listener.SQLListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class HamsterCurrency extends JavaPlugin {
    private static LogUtils logUtils;
    private IDataManager dataManager;
    private CurrencyListener listener;
    private boolean loaded;

    public static LogUtils getLogUtils() {
        return logUtils;
    }

    @Override
    public void onLoad() {
        FileManager.reload(this);
        logUtils = new LogUtils(this);
        logUtils.infoDividingLine();
        if (FileManager.isUseBC()) {
            logUtils.info("使用BC模式...");
            try {
                SQLDataManager sqlDataManager = new SQLDataManager(this);
                logUtils.info("SQL存档管理器初始化完成!");
                listener = new SQLListener(this, sqlDataManager);
                logUtils.info("事件监听器初始化完成!");
                dataManager = sqlDataManager;
            } catch (SQLException | ClassNotFoundException e) {
                logUtils.warning("插件加载时遇到了一个错误: ");
                e.printStackTrace();
                loaded = false;
            }
        }
        CurrencyAPI.setDataManager(dataManager);
        logUtils.info("API初始化完成!");
        loaded = true;
        logUtils.infoDividingLine();
    }

    @Override
    public void onEnable() {
        logUtils.infoDividingLine();
        if (!loaded) {
            logUtils.warning("插件未能成功启动!");
            setEnabled(false);
            return;
        }

        dataManager.onEnable();

        PluginCommand command = getCommand("HamsterCurrency");
        new CurrencyCommand(command, dataManager);
        logUtils.info("插件命令已注册!");

        Bukkit.getPluginManager().registerEvents(listener, this);
        logUtils.info("事件监听器已注册!");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            logUtils.info("检测到 PlaceholderAPI 已启动...");
            new PlaceholderHook(dataManager).register();
            logUtils.info("已挂载 PlaceholderAPI 变量!");
        } else {
            logUtils.info("未检测到 PlaceholderAPI!");
        }

        logUtils.info("插件已启动!");
        logUtils.infoDividingLine();
    }

    @Override
    public void onDisable() {
        logUtils.infoDividingLine();
        if (dataManager != null) {
            dataManager.onDisable();
        }
        logUtils.info("插件已关闭!");
        logUtils.infoDividingLine();
        logUtils.close();
    }
}
