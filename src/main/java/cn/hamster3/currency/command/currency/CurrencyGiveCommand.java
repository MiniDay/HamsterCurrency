package cn.hamster3.currency.command.currency;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

public class CurrencyGiveCommand extends CurrencyAdminSetCommand {
    public CurrencyGiveCommand(IDataManager dataManager) {
        super(
                dataManager,
                "give",
                "为玩家货币添加余额",
                "currency.give"
        );
    }

    @Override
    protected void doSet(PlayerData data, CurrencyType type, double amount) {
        data.setPlayerCurrency(type.getId(), data.getPlayerCurrency(type.getId()) + amount);
    }

}
