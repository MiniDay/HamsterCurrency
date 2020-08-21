package cn.hamster3.currency.data;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private String playerName;
    private HashMap<String, Double> playerCurrencies;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        playerName = Bukkit.getOfflinePlayer(uuid).getName();
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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerCurrency(String type, double amount) {
        playerCurrencies.put(type, amount);
    }

    public double getPlayerCurrency(String type) {
        return playerCurrencies.getOrDefault(type, 0D);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerData that = (PlayerData) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
