package cn.hamster3.currency.data;

import org.bukkit.configuration.ConfigurationSection;

/**
 * 货币类型
 */
public class CurrencyType {
    /**
     * 货币识别符
     */
    private final String id;
    /**
     * 是否允许转账
     */
    private final boolean canTransfer;

    public CurrencyType(ConfigurationSection config) {
        id = config.getName();
        canTransfer = config.getBoolean("canTransfer");
    }

    public String getId() {
        return id;
    }

    public boolean isCanTransfer() {
        return canTransfer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencyType that = (CurrencyType) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
