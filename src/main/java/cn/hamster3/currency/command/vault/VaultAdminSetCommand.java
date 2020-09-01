package cn.hamster3.currency.command.vault;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.command.CommandExecutor;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public abstract class VaultAdminSetCommand extends CommandExecutor {
    protected final IDataManager dataManager;

    public VaultAdminSetCommand(IDataManager dataManager, String name, String description, String permission) {
        super(
                name,
                description,
                permission,
                Message.notHasPermission.toString(),
                new String[]{
                        "玩家",
                        "数额"
                }
        );
        this.dataManager = dataManager;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    public abstract void doSet(PlayerData data, CurrencyType type, double amount);

    @Override
    @SuppressWarnings("DuplicatedCode")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CurrencyType type = dataManager.getCurrencyType(FileManager.getVaultCurrencyType());
        if (type == null) {
            sender.sendMessage(Message.vaultEconomySetError.toString());
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Message.notInputPlayerName.toString());
            return true;
        }
        PlayerData data = dataManager.getPlayerData(args[1]);
        if (data == null) {
            sender.sendMessage(Message.playerNotFound.toString());
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(Message.notInputAmount.toString());
            return true;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Message.amountNumberError.toString());
            return true;
        }
        doSet(data, type, amount);
        dataManager.savePlayerData(data);
        sender.sendMessage(
                Message.seeCurrency.toString()
                        .replace("%player%", data.getPlayerName())
                        .replace("%type%", type.getId())
                        .replace("%amount%", String.format("%.2f", data.getPlayerCurrency(type.getId())))
        );
        return true;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            List<String> types = dataManager.getPlayerData().stream().map(PlayerData::getPlayerName).collect(Collectors.toList());
            types = HamsterAPI.startWithIgnoreCase(types, args[0]);
            if (types.size() > 10) {
                types = types.subList(0, 9);
            }
            return types;
        }
        return null;
    }
}
