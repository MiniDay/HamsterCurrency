package cn.hamster3.currency.command;

import cn.hamster3.api.command.CommandManager;
import cn.hamster3.currency.core.IDataManager;
import org.bukkit.command.PluginCommand;

public class CurrencyCommand extends CommandManager {
    public CurrencyCommand(PluginCommand command, IDataManager dataManager) {
        super(command);
        addCommandExecutor(
                new AddCommand(dataManager),
                new PayCommand(dataManager),
                new ReloadCommand(dataManager),
                new SeeCommand(dataManager),
                new SetCommand(dataManager),
                new TakeCommand(dataManager),
                new TopCommand(dataManager)
        );
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

}
