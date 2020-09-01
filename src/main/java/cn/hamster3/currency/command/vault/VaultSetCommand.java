package cn.hamster3.currency.command.vault;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

public class VaultSetCommand extends VaultAdminSetCommand {
    public VaultSetCommand(IDataManager dataManager) {
        super(
                dataManager,
                "set",
                "设置玩家的金币",
                "currency.set"
        );
    }

    @Override
    public void doSet(PlayerData data, CurrencyType type, double amount) {
        data.setPlayerCurrency(type.getId(), amount);
    }
}
