package cn.hamster3.currency.core;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.currency.HamsterCurrency;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import cn.hamster3.service.spigot.HamsterService;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SQLDataManager implements IDataManager {
    private final JsonParser parser;
    private final Connection connection;
    private final HamsterCurrency plugin;
    private final HashSet<PlayerData> playerData;
    private final HashSet<CurrencyType> currencyTypes;

    @SuppressWarnings("ConstantConditions")
    public SQLDataManager(HamsterCurrency plugin) throws SQLException, ClassNotFoundException {
        this.plugin = plugin;
        parser = new JsonParser();
        playerData = new HashSet<>();
        currencyTypes = new HashSet<>();

        connection = HamsterAPI.getSQLConnection(FileManager.getPluginConfig().getConfigurationSection("datasource"));
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS hamster_currency_player_data(" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "data TEXT" +
                ");");
        statement.execute("CREATE TABLE IF NOT EXISTS hamster_currency_settings(" +
                "title VARCHAR(64) PRIMARY KEY," +
                "data TEXT" +
                ");");
        statement.close();
    }

    @Override
    public void onEnable() {
        HamsterCurrency.getLogUtils().info("从数据库中读取玩家数据...");
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT * FROM hamster_currency_player_data;");
            while (set.next()) {
                String string = set.getString("data");
                PlayerData data = new PlayerData(parser.parse(string).getAsJsonObject());
                playerData.add(data);
            }
            set.close();
            statement.close();
        } catch (SQLException e) {
            HamsterCurrency.getLogUtils().warning("从数据库中读取玩家数据时出现了一个异常:");
            e.printStackTrace();
        }
        HamsterCurrency.getLogUtils().info("从数据库中读取玩家数据完成!");

        if (FileManager.isMainServer()) {
            uploadConfigToSQL();
        } else {
            loadConfigFromSQL();
        }
    }

    @Override
    public void onDisable() {
        // 因为SQL模式使用HamsterService前置
        // 服务器之间数据实时同步
        // 所以关服时无需保存任何数据
    }

    @Override
    public void reload() {
        HamsterService.sendMessage("HamsterCurrency", "reload");
    }

    public void uploadConfigToSQL() {
        HamsterCurrency.getLogUtils().info("重载配置文件...");
        FileManager.reload(plugin);
        FileConfiguration config = FileManager.getPluginConfig();
        HamsterCurrency.getLogUtils().info("配置文件重载完成!");
        try {
            HamsterCurrency.getLogUtils().info("将配置文件上传至数据库...");
            Statement statement = connection.createStatement();
            String data = Base64.getEncoder().encodeToString(config.saveToString().getBytes(StandardCharsets.UTF_8));
            statement.executeUpdate(String.format(
                    "REPLACE INTO hamster_currency_settings VALUES('%s', '%s');",
                    "pluginConfig",
                    data
            ));
            statement.close();
            HamsterCurrency.getLogUtils().info("配置文件上传完成!");
        } catch (SQLException e) {
            HamsterCurrency.getLogUtils().info("插件上传 pluginConfig 至数据库时遇到了一个异常: ");
            e.printStackTrace();
        }
        loadConfig(config);
        HamsterService.sendMessage("HamsterCurrency", "uploadConfigToSQL %s", HamsterService.getServerName());
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public void loadConfigFromSQL() {
        try {
            HamsterCurrency.getLogUtils().info("从数据库中下载配置文件...");
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT * FROM hamster_currency_settings;");
            while (set.next()) {
                String title = set.getString("title");
                String data = new String(Base64.getDecoder().decode(set.getString("data")), StandardCharsets.UTF_8);
                switch (title) {
                    case "pluginConfig": {
                        YamlConfiguration config = new YamlConfiguration();
                        try {
                            config.loadFromString(data);
                        } catch (InvalidConfigurationException e) {
                            HamsterCurrency.getLogUtils().info("插件加载 %s 时遇到了一个异常: ", title);
                            e.printStackTrace();
                        }
                        loadConfig(config);
                    }
                }
            }
            statement.close();
            HamsterCurrency.getLogUtils().info("配置文件下载完成!");
        } catch (SQLException e) {
            HamsterCurrency.getLogUtils().info("插件从数据库中下载 pluginConfig 时遇到了一个异常: ");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void loadConfig(FileConfiguration config) {
        HamsterCurrency.getLogUtils().info("加载配置文件...");
        currencyTypes.clear();
        ConfigurationSection currencyTypesConfig = config.getConfigurationSection("currencyTypes");
        for (String key : currencyTypesConfig.getKeys(false)) {
            try {
                currencyTypes.add(new CurrencyType(currencyTypesConfig.getConfigurationSection(key)));
                HamsterCurrency.getLogUtils().warning("已加载货币类型: %s", key);
            } catch (Exception e) {
                HamsterCurrency.getLogUtils().warning("加载货币类型 %s 时出现了一个错误: ", key);
                e.printStackTrace();
            }
        }
        FileManager.setPluginConfig(config);
        HamsterCurrency.getLogUtils().info("配置文件加载完成!");
    }

    @Override
    public void loadPlayerData(UUID uuid) {
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(String.format(
                    "SELECT * FROM hamster_currency_player_data WHERE uuid='%s';",
                    uuid
            ));
            PlayerData data;
            if (set.next()) {
                String string = set.getString("data");
                data = new PlayerData(parser.parse(string).getAsJsonObject());
            } else {
                data = new PlayerData(uuid);
            }
            synchronized (SQLDataManager.class) {
                playerData.remove(data);
                playerData.add(data);
            }
            statement.close();
        } catch (SQLException e) {
            HamsterCurrency.getLogUtils().warning("加载玩家 %s 的存档数据时出错!", uuid);
            e.printStackTrace();
        }
    }

    @Override
    public void savePlayerData(PlayerData data) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                () -> {
                    try {
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(String.format(
                                "REPLACE INTO hamster_currency_player_data VALUES('%s', '%s');",
                                data.getUuid().toString(),
                                data.saveToJson().toString()
                        ));
                        statement.close();
                    } catch (SQLException e) {
                        HamsterCurrency.getLogUtils().warning("保存玩家 %s 的存档数据时出错!", data.getUuid());
                        e.printStackTrace();
                    }
                    HamsterService.sendMessage("HamsterCurrency", "savedPlayerData %s", data.getUuid());
                });
    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        synchronized (SQLDataManager.class) {
            for (PlayerData data : playerData) {
                if (data.getUuid().equals(uuid)) {
                    return data;
                }
            }
        }
        return null;
    }

    @Override
    public PlayerData getPlayerData(String name) {
        synchronized (SQLDataManager.class) {
            for (PlayerData data : playerData) {
                if (data.getPlayerName().equalsIgnoreCase(name)) {
                    return data;
                }
            }
        }
        return null;
    }

    @Override
    public Set<PlayerData> getPlayerData() {
        return playerData;
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
