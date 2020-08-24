package cn.hamster3.currency.core;

public enum Message {
    prefix("§a[仓鼠经济] "),
    notHasPermission("§c你没有这个权限!"),
    notInputPlayerName("请输入玩家名称!"),
    playerNotFound("未找到该玩家!"),
    notInputCurrencyType("请输入货币类型!"),
    currencyTypeNotFound("未找到该货币类型!"),
    notInputAmount("请输入货币额度!"),
    amountNumberError("货币额度必须是一个数字!"),
    playerCurrencySetSuccess("货币设置成功! 玩家 %player% 当前 %type% 余额为: %amount%"),
    currencyTypeCantTransfer("%type% 不支持转账!"),
    currencyNotEnough("你的 %type% 不足!"),
    paySuccess("已将 %amount% %type% 转账至 %player% 账户!"),
    receivePay("从 %player% 账户上收到 %amount% %type%."),
    seeCurrency("玩家 %player% 当前货币 %type% 余额为: %amount%"),
    pageError("页码必须是一个大于0的整数!"),
    topRankPageHead("========== %type% 排行榜 第 %page% 页 =========="),
    topRankPageItem("%rank%.%name% %amount%"),
    currencyNamePlural("金币"),
    currencyNameSingular("金币");
    private String message;

    Message(String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return prefix.message + message;
    }
}
