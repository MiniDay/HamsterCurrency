package cn.hamster3.currency;

import cn.hamster3.api.utils.LogUtils;
import cn.hamster3.currency.api.CurrencyAPI;
import cn.hamster3.currency.command.CurrencyCommand;
import cn.hamster3.currency.command.VaultPayCommand;
import cn.hamster3.currency.command.VaultSeeCommand;
import cn.hamster3.currency.command.VaultTopCommand;
import cn.hamster3.currency.core.FileDataManager;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.SQLDataManager;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.hook.PlaceholderHook;
import cn.hamster3.currency.hook.VaultEconomyHook;
import cn.hamster3.currency.listener.CurrencyListener;
import cn.hamster3.currency.listener.SQLListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
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
            logUtils.info("使用多服务器模式...");
            try {
                SQLDataManager sqlDataManager = new SQLDataManager(this);
                logUtils.info("SQL存档管理器初始化完成!");
                listener = new SQLListener(this, sqlDataManager);
                logUtils.info("事件监听器初始化完成!");
                dataManager = sqlDataManager;
            } catch (SQLException | ClassNotFoundException e) {
                logUtils.error("插件加载时遇到了一个错误: ", e);
                loaded = false;
            }
        } else {
            logUtils.info("使用单服务器模式...");
            FileDataManager fileDataManager = new FileDataManager(this);
            logUtils.info("文件存档管理器初始化完成!");
            listener = new CurrencyListener(this, fileDataManager);
            logUtils.info("事件监听器初始化完成!");
            dataManager = fileDataManager;
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

        dataManager.loadConfig();

        PluginCommand command = getCommand("HamsterCurrency");
        new CurrencyCommand(command, dataManager);
        logUtils.info("插件命令已注册!");

        Bukkit.getPluginManager().registerEvents(listener, this);
        logUtils.info("事件监听器已注册!");

        registerVault();

        logUtils.info("插件已启动!");
        logUtils.infoDividingLine();

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            dataManager.onEnable();
            for (Player player : Bukkit.getOnlinePlayers()) {
                dataManager.loadPlayerData(player.getUniqueId());
            }
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                logUtils.info("检测到 PlaceholderAPI 已启动...");
                new PlaceholderHook(dataManager).register();
                logUtils.info("已挂载 PlaceholderAPI 变量!");
            } else {
                logUtils.info("未检测到 PlaceholderAPI!");
            }
        });
    }

    private void registerVault() {
        logUtils.infoDividingLine();

        if (!FileManager.isVaultHook()) {
            Bukkit.getServicesManager().unregister(this);
            logUtils.info("不使用Vault经济系统挂接...");
            return;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            logUtils.warning("未找到 Vault 插件!  取消注册Vault经济系统...");
            return;
        }

        String type = FileManager.getVaultCurrencyType();
        logUtils.info("尝试以 %s 注册Vault经济系统...", type);

        CurrencyType currencyType = dataManager.getCurrencyType(type);
        if (currencyType == null) {
            logUtils.warning("未找到经济类型 %s! 取消注册Vault经济系统...", type);
            return;
        }

        VaultEconomyHook hook = new VaultEconomyHook(dataManager);
        logUtils.info("已初始化Vault连接器...");

        Bukkit.getServicesManager().register(Economy.class, hook, this, ServicePriority.Normal);
        logUtils.info("Vault经济系统注册成功!");

        new VaultPayCommand(getCommand("payMoney"), dataManager);
        new VaultSeeCommand(getCommand("balance"), dataManager);
        new VaultTopCommand(getCommand("balanceTop"), dataManager);

        logUtils.infoDividingLine();
    }

    @Override
    public void onDisable() {
        logUtils.infoDividingLine();
        if (dataManager != null) {
            dataManager.onDisable();
        }
        Bukkit.getServicesManager().unregister(this);
        logUtils.info("插件已关闭!");
        logUtils.infoDividingLine();
        logUtils.close();
    }
}
