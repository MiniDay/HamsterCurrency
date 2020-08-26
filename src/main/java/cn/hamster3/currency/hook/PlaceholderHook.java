package cn.hamster3.currency.hook;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {
    private final IDataManager dataManager;

    public PlaceholderHook(IDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "Currency";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "Hamster3";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        PlayerData data = dataManager.getPlayerData(player.getUniqueId());
        return String.format("%.2f", data.getPlayerCurrency(params));
    }
}
