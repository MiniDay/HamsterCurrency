package cn.hamster3.currency.listener;

import cn.hamster3.currency.HamsterCurrency;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.SQLDataManager;
import cn.hamster3.service.common.entity.ServiceMessageInfo;
import cn.hamster3.service.spigot.api.ServiceInfoAPI;
import cn.hamster3.service.spigot.event.MessageReceivedEvent;
import org.bukkit.event.EventHandler;

import java.util.UUID;

/**
 * 跨服模式时使用这个监听器
 */
public class SQLListener extends CurrencyListener {
    public SQLListener(HamsterCurrency plugin, SQLDataManager dataManager) {
        super(plugin, dataManager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onServiceReceive(MessageReceivedEvent event) {
        ServiceMessageInfo info = event.getMessageInfo();
        if (!"HamsterCurrency".equals(info.getTag())) {
            return;
        }
        SQLDataManager dataManager = (SQLDataManager) super.dataManager;
        switch (info.getAction()) {
            case "reload": {
                if (!FileManager.isMainServer()) {
                    return;
                }
                HamsterCurrency.getLogUtils().info("收到重载指令，开始重载服务器...");
                dataManager.uploadConfigToSQL();
                break;
            }
            case "uploadConfigToSQL": {
                if (ServiceInfoAPI.getLocalSenderInfo().equals(info.getSenderInfo())) {
                    return;
                }
                HamsterCurrency.getLogUtils().info("主服务器已上传 pluginConfig, 准备从数据库中下载配置并重载插件...");
                dataManager.loadConfigFromSQL();
                break;
            }
            case "savedPlayerData": {
                if (ServiceInfoAPI.getLocalSenderInfo().equals(info.getSenderInfo())) {
                    return;
                }
                UUID uuid = UUID.fromString(info.getContentAsString());
                dataManager.loadPlayerData(uuid);
                break;
            }
        }
    }
}
