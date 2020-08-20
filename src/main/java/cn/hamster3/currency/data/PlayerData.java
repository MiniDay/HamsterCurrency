package cn.hamster3.currency.data;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private HashMap<String, Double> playerCurrencies;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        playerCurrencies = new HashMap<>();
    }

    public PlayerData(JsonObject object) {

    }

    public JsonObject saveToJson() {
        return new JsonObject();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setPlayerCurrency(CurrencyType type, double amount) {
        playerCurrencies.put(type.getId(), amount);
    }

    public double getPlayerCurrency(CurrencyType type) {
        return playerCurrencies.getOrDefault(type.getId(), 0D);
    }

}
