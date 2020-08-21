package cn.hamster3.currency.command;

import cn.hamster3.api.command.CommandExecutor;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import cn.hamster3.service.spigot.HamsterService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PayCommand extends CommandExecutor {
    private final IDataManager dataManager;

    public PayCommand(IDataManager dataManager) {
        super(
                "pay",
                "向其他玩家转账",
                "currency.pay",
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
        return true;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Message.notInputPlayerName.toString());
            return true;
        }
        PlayerData toPlayerData = dataManager.getPlayerData(args[1]);
        if (toPlayerData == null) {
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
        if (type.isCanTransfer()) {
            sender.sendMessage(Message.currencyTypeCantTransfer.toString().replace("%type%", type.getId()));
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
        Player player = (Player) sender;
        PlayerData playerData = dataManager.getPlayerData(player.getUniqueId());
        if (playerData.getPlayerCurrency(type.getId()) < amount) {
            sender.sendMessage(
                    Message.currencyNotEnough.toString()
                            .replace("%type%", type.getId())
            );
            return true;
        }
        playerData.setPlayerCurrency(type.getId(), playerData.getPlayerCurrency(type.getId()) - amount);
        toPlayerData.setPlayerCurrency(type.getId(), toPlayerData.getPlayerCurrency(type.getId()) + amount);
        dataManager.savePlayerData(playerData);
        dataManager.savePlayerData(toPlayerData);
        sender.sendMessage(
                Message.paySuccess.toString()
                        .replace("%player%", toPlayerData.getPlayerName())
                        .replace("%type%", type.getId())
                        .replace("%amount%", String.format("%.2f", toPlayerData.getPlayerCurrency(type.getId())))
        );
        sender.sendMessage(
                Message.paySuccess.toString()
                        .replace("%player%", toPlayerData.getPlayerName())
                        .replace("%type%", type.getId())
                        .replace("%amount%", String.format("%.2f", toPlayerData.getPlayerCurrency(type.getId())))
        );
        if (FileManager.isUseBC()) {
            HamsterService.sendPlayerMessage(
                    toPlayerData.getUuid(),
                    Message.receivePay.toString()
                            .replace("%player%", player.getName())
                            .replace("%type%", type.getId())
                            .replace("%amount%", String.format("%.2f", toPlayerData.getPlayerCurrency(type.getId())))
            );
        } else {
            Player toPlayer = Bukkit.getPlayer(toPlayerData.getUuid());
            if (toPlayer != null) {
                toPlayer.sendMessage(
                        Message.receivePay.toString()
                                .replace("%player%", player.getName())
                                .replace("%type%", type.getId())
                                .replace("%amount%", String.format("%.2f", toPlayerData.getPlayerCurrency(type.getId())))
                );
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
