package cn.hamster3.currency.command.vault;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.command.CommandManager;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import cn.hamster3.service.spigot.HamsterService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class VaultPayCommand extends CommandManager {
    private final IDataManager dataManager;

    public VaultPayCommand(PluginCommand command, IDataManager dataManager) {
        super(command);
        this.dataManager = dataManager;
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean isPlayerCommand() {
        return true;
    }

    @Override
    public boolean checkPermission(CommandSender sender) {
        return sender.hasPermission("currency.pay");
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    protected boolean defaultCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!FileManager.isVaultHook()) {
            sender.sendMessage(Message.vaultEconomySetError.toString());
            return true;
        }
        CurrencyType type = dataManager.getCurrencyType(FileManager.getVaultCurrencyType());
        if (type == null) {
            sender.sendMessage(Message.vaultEconomySetError.toString());
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Message.notInputPlayerName.toString());
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(Message.notInputPayAmount.toString());
            return true;
        }

        PlayerData toData = dataManager.getPlayerData(args[0]);
        if (toData == null) {
            sender.sendMessage(Message.playerNotFound.toString());
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Message.amountNumberError.toString());
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(Message.amountNumberError.toString());
            return true;
        }

        Player player = (Player) sender;
        PlayerData fromData = dataManager.getPlayerData(player.getUniqueId());
        if (fromData.getPlayerCurrency(type.getId()) < amount) {
            sender.sendMessage(
                    Message.currencyNotEnough.toString()
                            .replace("%type%", type.getId())
            );
            return true;
        }

        fromData.setPlayerCurrency(type.getId(), fromData.getPlayerCurrency(type.getId()) - amount);
        toData.setPlayerCurrency(type.getId(), toData.getPlayerCurrency(type.getId()) + amount);
        dataManager.savePlayerData(fromData);
        dataManager.savePlayerData(toData);
        sender.sendMessage(
                Message.paySuccess.toString()
                        .replace("%player%", toData.getPlayerName())
                        .replace("%type%", type.getId())
                        .replace("%amount%", String.format("%.2f", amount))
        );
        if (FileManager.isUseBC()) {
            HamsterService.sendPlayerMessage(
                    toData.getUuid(),
                    Message.receivePay.toString()
                            .replace("%player%", player.getName())
                            .replace("%type%", type.getId())
                            .replace("%amount%", String.format("%.2f", amount))
            );
        } else {
            Player toPlayer = Bukkit.getPlayer(toData.getUuid());
            if (toPlayer != null) {
                toPlayer.sendMessage(
                        Message.receivePay.toString()
                                .replace("%player%", player.getName())
                                .replace("%type%", type.getId())
                                .replace("%amount%", String.format("%.2f", amount))
                );
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
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
