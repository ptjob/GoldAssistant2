package com.parttime.pojo;

/**
 * 刷新，加急的前置状态
 * Created by wyw on 2015/8/7.
 */
public enum PreCheckStatus {
    FREE(1),
    LACK_OF_MONEY(2),
    SHOULD_PAY(3),
    SHOULD_VERIFY(4),
    VERIFYING(5);

    int value;

    PreCheckStatus(int value) {
        this.value = value;
    }

    public static PreCheckStatus parse(int value) {
        switch (value) {
            case 2:
                return LACK_OF_MONEY;
            case 3:
                return SHOULD_PAY;
            case 4:
                return SHOULD_VERIFY;
            case 5:
                return VERIFYING;
            default:
                return FREE;
        }
    }
}
