package cn.hamster3.currency.api;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

import java.util.HashSet;
import java.util.UUID;

@SuppressWarnings("unused")
public abstract class CurrencyAPI {
    private static IDataManager dataManager;

    public static void setDataManager(IDataManager dataManager) {
        CurrencyAPI.dataManager = dataManager;
    }

    public double getPlayerCurrency(UUID uuid, String currencyID) {
        PlayerData data = dataManager.getPlayerData(uuid);
        if (data == null) {
            return 0;
        }
        return data.getPlayerCurrency(currencyID);
    }

    public void setPlayerCurrency(UUID uuid, String currencyID, double amount) {
        PlayerData data = dataManager.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        data.setPlayerCurrency(currencyID, amount);
        dataManager.savePlayerData(data);
    }

    public void addPlayerCurrency(UUID uuid, String currencyID, double amount) {
        PlayerData data = dataManager.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        data.setPlayerCurrency(currencyID, data.getPlayerCurrency(currencyID) + amount);
        dataManager.savePlayerData(data);
    }

    public void takePlayerCurrency(UUID uuid, String currencyID, double amount) {
        PlayerData data = dataManager.getPlayerData(uuid);
        if (data == null) {
            return;
        }
        data.setPlayerCurrency(currencyID, data.getPlayerCurrency(currencyID) - amount);
        dataManager.savePlayerData(data);
    }

    public HashSet<CurrencyType> getAllCurrencyType() {
        return new HashSet<>();
    }

}
