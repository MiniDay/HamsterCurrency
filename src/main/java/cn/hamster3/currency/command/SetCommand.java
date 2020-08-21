package cn.hamster3.currency.command;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

public class SetCommand extends AdminSetCommand {
    public SetCommand(IDataManager dataManager) {
        super(
                dataManager,
                "set",
                "为玩家货币设置余额",
                "currency.set",
                Message.notHasPermission.toString()
        );
    }

    @Override
    protected void doSet(PlayerData data, CurrencyType type, double amount) {
        data.setPlayerCurrency(type.getId(), amount);
    }

}
