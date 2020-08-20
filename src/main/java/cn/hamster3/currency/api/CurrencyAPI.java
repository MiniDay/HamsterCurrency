package cn.hamster3.currency.api;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.CurrencyType;

import java.util.HashSet;
import java.util.UUID;

public class CurrencyAPI {
    private IDataManager dataManager;

    public double getPlayerCurrency(UUID uuid, String currencyID) {
        return 0.0D;
    }

    public void setPlayerCurrency(UUID uuid, String currencyID) {
    }

    public void addPlayerCurrency(UUID uuid, String currencyID) {
    }

    public void takePlayerCurrency(UUID uuid, String currencyID) {
    }

    public HashSet<CurrencyType> getAllCurrencyType() {
        return new HashSet<>();
    }

}
