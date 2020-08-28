package cn.hamster3.currency.command.currency;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

public class CurrencySetCommand extends CurrencyAdminSetCommand {
    public CurrencySetCommand(IDataManager dataManager) {
        super(
                dataManager,
                "set",
                "为玩家货币设置余额",
                "currency.set"
        );
    }

    @Override
    protected void doSet(PlayerData data, CurrencyType type, double amount) {
        data.setPlayerCurrency(type.getId(), amount);
    }

}
