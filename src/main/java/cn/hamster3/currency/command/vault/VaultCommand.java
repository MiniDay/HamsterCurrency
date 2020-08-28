package cn.hamster3.currency.command.vault;

import cn.hamster3.api.command.CommandManager;
import cn.hamster3.currency.core.IDataManager;
import org.bukkit.command.PluginCommand;

public class VaultCommand extends CommandManager {
    public VaultCommand(PluginCommand command, IDataManager dataManager) {
        super(command);
        addCommandExecutor(
                new VaultSetCommand(dataManager),
                new VaultGiveCommand(dataManager),
                new VaultTakeCommand(dataManager)
        );
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

}
