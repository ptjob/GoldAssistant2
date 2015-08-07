package com.parttime.pojo;

/**
 * 发布条件状态
 * Created by wyw on 2015/8/7.
 */
public enum PublishAvailabilityStatus {
    SUCCESS(1),
    SHOULD_VERIFY(2),
    VERIFYING(3),
    LOCK_OF_MONEY(4),
    SHOULD_PAY(5);

    int value;

    PublishAvailabilityStatus(int value) {
        this.value = value;
    }

    public static PublishAvailabilityStatus parse(int value) {
        switch (value) {
            case 2:
                return SHOULD_VERIFY;
            case 3:
                return VERIFYING;
            case 4:
                return LOCK_OF_MONEY;
            case 5:
                return SHOULD_PAY;
            default:
                return SUCCESS;
        }
    }
}
