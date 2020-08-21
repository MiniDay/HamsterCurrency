package cn.hamster3.currency.hook;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderHook extends PlaceholderExpansion {
    private final IDataManager dataManager;

    public PlaceholderHook(IDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public String getIdentifier() {
        return "Currency";
    }

    @Override
    public String getAuthor() {
        return "Hamster3";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        PlayerData data = dataManager.getPlayerData(player.getUniqueId());
        return String.format("%.2f", data.getPlayerCurrency(params));
    }
}
