package com.parttime.pojo;

/**
 * 发布条件状态
 * Created by wyw on 2015/8/7.
 */
public enum PreRefreshStatus {
    FREE(1),
    LACK_OF_MONEY(2),
    SHOULD_PAY(3),
    SHOULD_VERIFY(4);

    int value;

    PreRefreshStatus(int value) {
        this.value = value;
    }

    public static PreRefreshStatus parse(int value) {
        switch (value) {
            case 2:
                return LACK_OF_MONEY;
            case 3:
                return SHOULD_PAY;
            case 4:
                return SHOULD_VERIFY;
            default:
                return FREE;
        }
    }
}
