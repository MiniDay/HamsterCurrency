package cn.hamster3.currency.command.vault;

import cn.hamster3.api.command.CommandManager;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;

public class VaultTopCommand extends CommandManager {
    private final IDataManager dataManager;

    public VaultTopCommand(PluginCommand command, IDataManager dataManager) {
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
        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
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
}
