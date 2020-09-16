package cn.hamster3.currency.command.currency;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.command.CommandExecutor;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CurrencyAdminSetCommand extends CommandExecutor {
    protected final IDataManager dataManager;

    public CurrencyAdminSetCommand(IDataManager dataManager, String name, String description, String permission) {
        super(
                name,
                description,
                permission,
                Message.notHasPermission.toString(),
                new String[]{
                        "玩家",
                        "货币类型",
                        "数额"
                }
        );
        this.dataManager = dataManager;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    @Override
    public String getPermissionMessage() {
        return Message.notHasPermission.toString();
    }

    protected abstract void doSet(PlayerData data, CurrencyType type, double amount);

    @Override
    @SuppressWarnings("DuplicatedCode")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
            sender.sendMessage(Message.notInputCurrencyType.toString());
            return true;
        }
        CurrencyType type = dataManager.getCurrencyType(args[2]);
        if (type == null) {
            sender.sendMessage(Message.currencyTypeNotFound.toString());
            return true;
        }
        if (args.length < 4) {
            sender.sendMessage(Message.notInputAmount.toString());
            return true;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Message.amountNumberError.toString());
            return true;
        }
        doSet(data, type, amount);
        sender.sendMessage(
                Message.playerCurrencySetSuccess.toString()
                        .replace("%player%", data.getPlayerName())
                        .replace("%type%", type.getId())
                        .replace("%amount%", String.format("%.2f", data.getPlayerCurrency(type.getId())))
        );
        dataManager.savePlayerData(data);
        return true;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 2: {
                return HamsterAPI.getOnlinePlayersName(args[1]);
            }
            case 3: {
                List<String> types = dataManager.getCurrencyTypes().stream().map(CurrencyType::getId).collect(Collectors.toList());
                types = HamsterAPI.startWithIgnoreCase(types, args[2]);
                if (types.size() > 10) {
                    types = types.subList(0, 9);
                }
                return types;
            }
        }
        return null;
    }
}
