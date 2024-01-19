package com.firstone.greenjangteo.reserve.exception.message;

public class InsufficientExceptionMessage {
    public static final String INSUFFICIENT_CURRENT_RESERVE_EXCEPTION
            = "현재 적립금이 음수이므로 적립금을 사용할 수 없습니다. 현재 적립금 액수: ";

    public static final String INSUFFICIENT_NEW_RESERVE_EXCEPTION1
            = "사용하려는 적립금 액수가 현재 적립금 액수보다 많습니다. 현재 적립금 액수: ";
    public static final String INSUFFICIENT_NEW_RESERVE_EXCEPTION2
            = ", 사용하려는 적립금 액수: ";
}
