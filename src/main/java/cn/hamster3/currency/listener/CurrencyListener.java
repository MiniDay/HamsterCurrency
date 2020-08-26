package cn.hamster3.currency.listener;

import cn.hamster3.currency.HamsterCurrency;
import cn.hamster3.currency.core.IDataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CurrencyListener implements Listener {
    protected final HamsterCurrency plugin;
    protected final IDataManager dataManager;

    public CurrencyListener(HamsterCurrency plugin, IDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> dataManager.loadPlayerData(event.getPlayer().getUniqueId()));
    }
}
