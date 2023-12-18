package com.firstone.greenjangteo.coupon.excpeption.message;

public class InvalidExceptionMessage {
    public static final String INVALID_COUPON_AMOUNT_EXCEPTION
            = "쿠폰 금액은 양의 정수(1 이상의 숫자)여야 합니다. 입력된 금액: ";
    public static final String INVALID_ISSUE_QUANTITY_EXCEPTION
            = "쿠폰 발행 매수는 양의 정수(1 이상의 숫자)여야 합니다. 입력된 발행 매수: ";
    public static final String INVALID_EXPIRATION_PERIOD_EXCEPTION
            = "쿠폰 유효기간은 양의 정수(1 이상의 숫자)여야 합니다. 입력된 유효기간: ";
}
