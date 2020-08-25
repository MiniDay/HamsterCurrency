package cn.hamster3.currency.command;

import cn.hamster3.api.command.CommandManager;
import cn.hamster3.currency.core.FileManager;
import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.SQLDataManager;
import org.bukkit.command.PluginCommand;

public class CurrencyCommand extends CommandManager {
    public CurrencyCommand(PluginCommand command, IDataManager dataManager) {
        super(command);
        addCommandExecutor(
                new CurrencyAddCommand(dataManager),
                new CurrencyPayCommand(dataManager),
                new ReloadCommand(dataManager),
                new CurrencySeeCommand(dataManager),
                new CurrencySetCommand(dataManager),
                new CurrencyTakeCommand(dataManager),
                new CurrencyTopCommand(dataManager)
        );
        if (FileManager.isUseBC()) {
            addCommandExecutor(new CurrencyImportCommand((SQLDataManager) dataManager));
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

}
