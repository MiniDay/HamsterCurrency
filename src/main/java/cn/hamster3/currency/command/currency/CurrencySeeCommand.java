package cn.hamster3.currency.command.currency;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.command.CommandExecutor;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CurrencySeeCommand extends CommandExecutor {
    private final IDataManager dataManager;

    public CurrencySeeCommand(IDataManager dataManager) {
        super(
                "see",
                "查看玩家的货币余额",
                "currency.see",
                Message.notHasPermission.toString(),
                new String[]{
                        "货币类型",
                        "玩家"
                }
        );
        this.dataManager = dataManager;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Message.notInputCurrencyType.toString());
            return true;
        }
        CurrencyType type = dataManager.getCurrencyType(args[1]);
        if (type == null) {
            sender.sendMessage(Message.currencyTypeNotFound.toString());
            return true;
        }
        PlayerData data;
        if (args.length < 3) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Message.notInputPlayerName.toString());
                return true;
            }
            Player player = (Player) sender;
            data = dataManager.getPlayerData(player.getUniqueId());
        } else {
            if (!sender.hasPermission("currency.see.other")) {
                sender.sendMessage(Message.notHasPermission.toString());
                return true;
            }
            data = dataManager.getPlayerData(args[2]);
            if (data == null) {
                sender.sendMessage(Message.playerNotFound.toString());
                return true;
            }
        }
        sender.sendMessage(
                Message.seeCurrency.toString()
                        .replace("%player%", data.getPlayerName())
                        .replace("%type%", type.getId())
                        .replace("%amount%", String.format("%.2f", data.getPlayerCurrency(type.getId())))
        );
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            List<String> types = dataManager.getCurrencyTypes().stream().map(CurrencyType::getId).collect(Collectors.toList());
            return HamsterAPI.startWith(types, args[1]);
        }
        if (args.length == 3) {
            return HamsterAPI.getOnlinePlayersName(args[2]);
        }
        return null;
    }
}
