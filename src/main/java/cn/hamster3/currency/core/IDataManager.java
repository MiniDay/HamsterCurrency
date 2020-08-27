package cn.hamster3.currency.core;

import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

import java.util.Set;
import java.util.UUID;

public interface IDataManager {

    /**
     * 插件启动时调用
     * 此时应该加载全部玩家的数据
     */
    void onEnable();

    /**
     * 插件关闭时调用
     * 此时应该保存全部玩家的数据
     */
    void onDisable();

    void loadConfig();

    /**
     * 重载服务器
     */
    void reloadConfig();

    /**
     * 加载玩家的数据
     *
     * @param uuid -
     */
    void loadPlayerData(UUID uuid);

    /**
     * 保存玩家的数据
     *
     * @param data -
     */
    void savePlayerData(PlayerData data);

    PlayerData getPlayerData(UUID uuid);

    PlayerData getPlayerData(String name);

    Set<PlayerData> getPlayerData();

    CurrencyType getCurrencyType(String id);

    Set<CurrencyType> getCurrencyTypes();
}
