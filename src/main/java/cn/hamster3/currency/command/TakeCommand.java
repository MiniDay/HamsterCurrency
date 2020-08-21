package cn.hamster3.currency.command;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.core.Message;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

public class TakeCommand extends AdminSetCommand {
    public TakeCommand(IDataManager dataManager) {
        super(
                dataManager,
                "take",
                "为玩家货币扣除余额",
                "currency.take",
                Message.notHasPermission.toString()
        );
    }

    @Override
    protected void doSet(PlayerData data, CurrencyType type, double amount) {
        data.setPlayerCurrency(type.getId(), data.getPlayerCurrency(type.getId()) - amount);
    }

}
