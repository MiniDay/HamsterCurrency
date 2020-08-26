package cn.hamster3.currency.command;

import cn.hamster3.api.command.CommandExecutor;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.core.SQLDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CurrencyImportCommand extends CommandExecutor {
    private final SQLDataManager dataManager;

    public CurrencyImportCommand(SQLDataManager dataManager) {
        super(
                "import",
                "从其他插件中导入数据",
                "currency.import",
                Message.notHasPermission.toString(),
                new String[]{
                        "数据库",
                        "数据表",
                        "uuid列名",
                        "name列名",
                        "money列名",
                        "货币类型"
                }
        );
        this.dataManager = dataManager;
    }

    @Override
    public boolean isPlayerCommand() {
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 7) {
            sender.sendMessage("§a/currency import [数据库] [数据表] [uuid列名] [name列名] [money列名] [货币类型]");
            return true;
        }
        dataManager.importFromOtherPluginData(args[1], args[2], args[3], args[4], args[5], args[6]);
        sender.sendMessage("§a已开始从其他插件的数据库存档中导入数据, 详情请查看控制台输出...");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
