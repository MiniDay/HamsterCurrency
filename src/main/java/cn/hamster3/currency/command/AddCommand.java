package cn.hamster3.currency.command;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

public class AddCommand extends AdminSetCommand {
    public AddCommand(IDataManager dataManager) {
        super(
                dataManager,
                "add",
                "为玩家货币添加余额",
                "currency.add",
                Message.notHasPermission.toString()
        );
    }

    @Override
    protected void doSet(PlayerData data, CurrencyType type, double amount) {
        data.setPlayerCurrency(type.getId(), data.getPlayerCurrency(type.getId()) + amount);
    }

}
