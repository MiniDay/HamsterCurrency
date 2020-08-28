package cn.hamster3.currency.command.vault;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

public class VaultTakeCommand extends VaultAdminSetCommand {
    public VaultTakeCommand(IDataManager dataManager) {
        super(
                dataManager,
                "take",
                "扣除玩家金币",
                "currency.take"
        );
    }

    @Override
    public void doSet(PlayerData data, CurrencyType type, double amount) {
        data.setPlayerCurrency(type.getId(), data.getPlayerCurrency(type.getId()) - amount);
    }
}
