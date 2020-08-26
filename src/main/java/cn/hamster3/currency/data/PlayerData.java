package cn.hamster3.currency.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final String playerName;
    private final HashMap<String, Double> playerCurrencies;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        playerName = Bukkit.getOfflinePlayer(uuid).getName();
        playerCurrencies = new HashMap<>();
    }

    public PlayerData(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        playerCurrencies = new HashMap<>();
    }

    public PlayerData(JsonObject object) {
        uuid = UUID.fromString(object.get("uuid").getAsString());
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player.getName() != null) {
            playerName = player.getName();
        } else if (object.has("playerName") && !object.get("playerName").isJsonNull()) {
            playerName = object.get("playerName").getAsString();
        } else {
            playerName = null;
        }
        playerCurrencies = new HashMap<>();
        JsonObject playerCurrenciesJson = object.getAsJsonObject("playerCurrencies");
        for (Map.Entry<String, JsonElement> entry : playerCurrenciesJson.entrySet()) {
            playerCurrencies.put(entry.getKey(), entry.getValue().getAsDouble());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public PlayerData(ConfigurationSection config) {
        uuid = UUID.fromString(config.getString("uuid"));
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player.getName() != null) {
            playerName = player.getName();
        } else {
            playerName = config.getString("playerName");
        }
        playerCurrencies = new HashMap<>();
        ConfigurationSection playerCurrenciesConfig = config.getConfigurationSection("playerCurrencies");
        for (String key : playerCurrenciesConfig.getKeys(false)) {
            playerCurrencies.put(key, playerCurrenciesConfig.getDouble(key));
        }
    }

    public JsonObject saveToJson() {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("playerName", playerName);
        JsonObject playerCurrenciesJson = new JsonObject();
        for (Map.Entry<String, Double> entry : playerCurrencies.entrySet()) {
            playerCurrenciesJson.addProperty(entry.getKey(), entry.getValue());
        }
        object.add("playerCurrencies", playerCurrenciesJson);
        return object;
    }

    public YamlConfiguration saveToConfig() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("uuid", uuid.toString());
        config.set("playerName", playerName);
        YamlConfiguration playerCurrenciesConfig = new YamlConfiguration();
        for (Map.Entry<String, Double> entry : playerCurrencies.entrySet()) {
            playerCurrenciesConfig.set(entry.getKey(), entry.getValue());
        }
        config.set("playerCurrencies", playerCurrenciesConfig);
        return config;
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
