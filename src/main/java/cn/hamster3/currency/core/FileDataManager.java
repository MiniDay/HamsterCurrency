package cn.hamster3.currency.core;

import cn.hamster3.currency.HamsterCurrency;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static cn.hamster3.currency.HamsterCurrency.getLogUtils;

public class FileDataManager implements IDataManager {
    private final HamsterCurrency plugin;
    private final HashSet<PlayerData> playerData;
    private final HashSet<CurrencyType> currencyTypes;

    public FileDataManager(HamsterCurrency plugin) {
        this.plugin = plugin;
        playerData = new HashSet<>();
        currencyTypes = new HashSet<>();
    }

    @Override
    public void onEnable() {
        getLogUtils().info("从本地磁盘中读取玩家数据...");

        File dataFolder = new File(plugin.getDataFolder(), "PlayerData");
        if (dataFolder.mkdirs()) {
            getLogUtils().info("创建玩家存档文件夹...");
        }

        File[] files = dataFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    PlayerData data = new PlayerData(YamlConfiguration.loadConfiguration(file));
                    playerData.add(data);
                } catch (Exception e) {
                    getLogUtils().error(e, "加载玩家存档文件 %s 时出现了一个异常!", file.getName());
                }
            }
        }
        getLogUtils().info("从本地磁盘中读取玩家数据完成!");
    }

    @Override
    public void onDisable() {
        File dataFolder = new File(plugin.getDataFolder(), "PlayerData");
        for (PlayerData data : playerData) {
            File dataFile = new File(dataFolder, data.getUuid().toString() + ".yml");
            try {
                data.saveToConfig().save(dataFile);
            } catch (IOException e) {
                getLogUtils().error(e, "保存玩家 %s 的存档至文件时出现了一个异常!", data.getUuid());
            }
        }
    }

    @Override
    public void loadConfig() {
        reloadConfig();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void reloadConfig() {
        getLogUtils().info("加载配置文件...");

        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        currencyTypes.clear();
        ConfigurationSection currencyTypesConfig = config.getConfigurationSection("currencyTypes");
        for (String key : currencyTypesConfig.getKeys(false)) {
            try {
                currencyTypes.add(new CurrencyType(currencyTypesConfig.getConfigurationSection(key)));
                getLogUtils().warning("已加载货币类型: %s", key);
            } catch (Exception e) {
                getLogUtils().error(e, "加载货币类型 %s 时出现了一个错误: ", key);
            }
        }
        FileManager.setPluginConfig(config);
        getLogUtils().info("配置文件加载完成!");
    }

    @Override
    public void loadPlayerData(UUID uuid) {
        // 由于服务器启动时已经加载了所有玩家的存档
        // 所以当玩家进服时无需再加载
        // 只需要给没有存档的新玩家初始化一个存档数据即可
        PlayerData data = getPlayerData(uuid);
        if (data == null) {
            playerData.add(new PlayerData(uuid));
        }
    }

    @Override
    public void savePlayerData(PlayerData data) {
        // 每一次修改存档都保存至磁盘一次会极大地浪费服务器性能
        // 按照插件架构，我们只需要在关服的时候保存所有玩家的存档即可
        // 所以这里什么都不做
    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        synchronized (playerData) {
            for (PlayerData data : playerData) {
                if (uuid.equals(data.getUuid())) {
                    return data;
                }
            }
        }
        return null;
    }

    @Override
    public PlayerData getPlayerData(String name) {
        synchronized (playerData) {
            for (PlayerData data : playerData) {
                if (name.equals(data.getPlayerName())) {
                    return data;
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<PlayerData> getPlayerData() {
        synchronized (playerData) {
            return new ArrayList<>(playerData);
        }
    }

    @Override
    public CurrencyType getCurrencyType(String id) {
        for (CurrencyType type : currencyTypes) {
            if (type.getId().equalsIgnoreCase(id)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public Set<CurrencyType> getCurrencyTypes() {
        return currencyTypes;
    }
}
