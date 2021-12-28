package com.kclm.xsap.consts;

/**
 * @author fangkai
 * @description
 * @create 2021-12-20 22:52
 */
public enum OperateType {
    RECHARGE_OPERATION(0, "充值操作"),
    BINDING_CARD_OPERATION(1, "绑卡操作"),
    CLASS_DEDUCTION(2, "会员上课扣费"),
    BIND_CARD_RECHARGE_OPERATION(3, "绑卡充值操作");

    private final int code;
    private final String msg;

    OperateType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
