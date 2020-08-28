package cn.hamster3.currency.command.vault;

import cn.hamster3.api.HamsterAPI;
import cn.hamster3.api.command.CommandManager;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class VaultSeeCommand extends CommandManager {
    private final IDataManager dataManager;

    public VaultSeeCommand(PluginCommand command, IDataManager dataManager) {
        super(command);
        this.dataManager = dataManager;
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
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

        PlayerData data;
        if (args.length < 1) {
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
            data = dataManager.getPlayerData(args[0]);
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
