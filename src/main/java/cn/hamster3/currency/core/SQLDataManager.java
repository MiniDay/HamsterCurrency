package cn.hamster3.currency.core;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.currency.HamsterCurrency;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import com.google.gson.JsonParser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    public SQLDataManager(HamsterCurrency plugin) throws SQLException {
        this.plugin = plugin;
        parser = new JsonParser();
        playerData = new HashSet<>();
        currencyTypes = new HashSet<>();

        connection = HamsterAPI.getMySQLConnection(FileManager.getPluginConfig().getConfigurationSection("mysql"));
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
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // 因为SQL模式使用HamsterService前置
        // 服务器之间数据实时同步
        // 所以关服时无需保存任何数据
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    public void reload() {

        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT * FROM hamster_currency_settings;");
            while (set.next()) {
                String title = set.getString("title");
                switch (title) {
                    case "pluginConfig": {

                    }
                }
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadPlayerData(UUID uuid) {
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(String.format(
                    "SELECT * FROM glazed_time_player_data WHERE uuid='%s';",
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
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format(
                    "REPLACE INTO glazed_time_player_data VALUES('%s', '%s');",
                    data.getUuid().toString(),
                    data.saveToJson().toString()
            ));
            statement.close();
        } catch (SQLException e) {
            HamsterCurrency.getLogUtils().warning("保存玩家 %s 的存档数据时出错!", data.getUuid());
            e.printStackTrace();
        }
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
