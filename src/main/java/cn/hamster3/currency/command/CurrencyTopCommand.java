package cn.hamster3.currency.command;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.command.CommandExecutor;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyTopCommand extends CommandExecutor {
    private final IDataManager dataManager;

    public CurrencyTopCommand(IDataManager dataManager) {
        super(
                "top",
                "查看玩家的货币余额排行榜",
                "currency.top",
                Message.notHasPermission.toString(),
                new String[]{
                        "货币类型",
                        "页码"
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
        int page = 1;
        if (args.length >= 3) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.pageError.toString());
                return true;
            }
        }

        page = page - 1;
        String typeId = type.getId();

        ArrayList<PlayerData> playerData = new ArrayList<>(dataManager.getPlayerData());
        playerData.sort((o1, o2) -> -Double.compare(o1.getPlayerCurrency(typeId), o2.getPlayerCurrency(typeId)));

        sender.sendMessage(
                Message.topRankPageHead.toString()
                        .replace("%type%", typeId)
                        .replace("%page%", String.valueOf(page + 1))
        );
        for (int i = page * 10; i < (page + 1) * 10; i++) {
            if (i >= playerData.size()) {
                break;
            }
            PlayerData data = playerData.get(i);
            sender.sendMessage(
                    Message.topRankPageItem.toString()
                            .replace("%rank%", String.valueOf(i + 1))
                            .replace("%name%", data.getPlayerName())
                            .replace("%amount%", String.format("%.2f", data.getPlayerCurrency(typeId)))
            );
        }
        return true;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            List<String> types = dataManager.getPlayerData().stream().map(PlayerData::getPlayerName).collect(Collectors.toList());
            types = HamsterAPI.startWithIgnoreCase(types, args[1]);
            if (types.size() > 10) {
                types = types.subList(0, 9);
            }
            return types;
        }
        return null;
    }
}
