package cn.hamster3.currency.command.vault;

import cn.hamster3.currency.core.IDataManager;
import cn.hamster3.currency.data.CurrencyType;
import cn.hamster3.currency.data.PlayerData;

public class VaultGiveCommand extends VaultAdminSetCommand {
    public VaultGiveCommand(IDataManager dataManager) {
        super(
                dataManager,
                "give",
                "给予玩家金币",
                "currency.give"
        );
    }

    @Override
    public void doSet(PlayerData data, CurrencyType type, double amount) {
        data.setPlayerCurrency(type.getId(), data.getPlayerCurrency(type.getId()) + amount);
    }
}
