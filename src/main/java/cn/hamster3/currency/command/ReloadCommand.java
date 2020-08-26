package cn.hamster3.currency.command;

import cn.hamster3.api.command.CommandExecutor;
import cn.hamster3.currency.core.IDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends CommandExecutor {
    private final IDataManager dataManager;

    public ReloadCommand(IDataManager dataManager) {
        super("reload", "重载服务器");
        this.dataManager = dataManager;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        dataManager.reloadConfig();
        sender.sendMessage("§a插件重载完成!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
