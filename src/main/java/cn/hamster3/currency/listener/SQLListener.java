package cn.hamster3.currency.listener;

import cn.hamster3.currency.HamsterCurrency;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.SQLDataManager;
import cn.hamster3.service.spigot.HamsterService;
import cn.hamster3.service.spigot.event.ServiceReceiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * 跨服模式时使用这个监听器
 */
public class SQLListener extends CurrencyListener {
    private final HamsterCurrency plugin;
    private final SQLDataManager dataManager;

    public SQLListener(HamsterCurrency plugin, SQLDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> dataManager.loadPlayerData(event.getPlayer().getUniqueId()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onServiceReceive(ServiceReceiveEvent event) {
        if (!"HamsterCurrency".equals(event.getTag())) {
            return;
        }
        String[] args = event.getMessage().split(" ");
        switch (args[0]) {
            case "reload": {
                if (!FileManager.isMainServer()) {
                    return;
                }
                dataManager.uploadConfigToSQL();
                break;
            }
            case "uploadConfigToSQL": {
                if (HamsterService.getServerName().equals(args[1])) {
                    return;
                }
                dataManager.loadConfigFromSQL();
                break;
            }
            case "savedPlayerData": {
                UUID uuid = UUID.fromString(args[1]);
                dataManager.loadPlayerData(uuid);
                break;
            }
        }
    }
}
