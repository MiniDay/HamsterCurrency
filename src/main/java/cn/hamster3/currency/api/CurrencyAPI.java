package cn.hamster3.currency.api;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

/**
 * 当 currencyID 为 PlayerPoints 且服务器安装了点券插件时，会自动更改为 PlayerPoints 接口
 */
@SuppressWarnings({"unused", "ConstantConditions"})
public abstract class CurrencyAPI {
    private static IDataManager dataManager;

    public static void setDataManager(IDataManager dataManager) {
        CurrencyAPI.dataManager = dataManager;
    }

    public static double getPlayerCurrency(UUID uuid, String currencyID) {
        if (currencyID.equals("PlayerPoints") && Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            return ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI().look(uuid);
        }
        PlayerData data = dataManager.getPlayerData(uuid);
        if (data == null) {
            return 0;
        }
        return data.getPlayerCurrency(currencyID);
    }

    public static void setPlayerCurrency(UUID uuid, String currencyID, double amount) {
        if (currencyID.equals("PlayerPoints") && Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI().set(uuid, (int) amount);
            return;
        }
        PlayerData data = dataManager.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        data.setPlayerCurrency(currencyID, amount);
        dataManager.savePlayerData(data);
    }

    public static void addPlayerCurrency(UUID uuid, String currencyID, double amount) {
        if (currencyID.equals("PlayerPoints") && Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI().give(uuid, (int) amount);
            return;
        }
        PlayerData data = dataManager.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        data.setPlayerCurrency(currencyID, data.getPlayerCurrency(currencyID) + amount);
        dataManager.savePlayerData(data);
    }

    public static void takePlayerCurrency(UUID uuid, String currencyID, double amount) {
        if (currencyID.equals("PlayerPoints") && Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI().take(uuid, (int) amount);
            return;
        }
        PlayerData data = dataManager.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        data.setPlayerCurrency(currencyID, data.getPlayerCurrency(currencyID) - amount);
        dataManager.savePlayerData(data);
    }

    public static boolean hasPlayerCurrency(UUID uuid, String currencyID, double amount) {
        if (currencyID.equals("PlayerPoints") && Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            return ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI().look(uuid) >= amount;
        }
        PlayerData data = dataManager.getPlayerData(uuid);
        if (data == null) {
            return false;
        }
        return data.getPlayerCurrency(currencyID) >= amount;
    }

    public ArrayList<CurrencyType> getAllCurrencyType() {
        return new ArrayList<>(dataManager.getCurrencyTypes());
    }

}
