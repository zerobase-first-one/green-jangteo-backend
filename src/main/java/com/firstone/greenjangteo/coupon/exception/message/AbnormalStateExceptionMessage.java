package com.firstone.greenjangteo.coupon.exception.message;

public class AbnormalStateExceptionMessage {
    public static final String INCONSISTENT_COUPON_SIZE_EXCEPTION
            = "지급 예정 쿠폰 수량이 필요로 하는 쿠폰 수량과 일치하지 않습니다. 지급 예정 쿠폰 수량: ";
    public static final String INCONSISTENT_COUPON_SIZE_EXCEPTION_REQUIRED_QUANTITY = ", 필요로 하는 쿠폰 수량: ";
    public static final String INSUFFICIENT_REMAINING_QUANTITY_EXCEPTION
            = "쿠폰 그룹의 잔여 쿠폰 수량이 지급 예정 쿠폰 수량보다 부족합니다. 잔여 쿠폰 수량: ";
    public static final String INSUFFICIENT_REMAINING_QUANTITY_EXCEPTION_QUANTITY_TO_PROVIDE = ", 지급 예정 쿠폰 수량: ";
    public static final String ALREADY_GIVEN_COUPON_EXCEPTION = "이미 다른 사용자에게 지급된 쿠폰입니다. 회원 ID: ";
    public static final String ALREADY_USED_COUPON_EXCEPTION = "이미 사용된 쿠폰입니다. 사용된 주문 ID: ";
    public static final String NOT_USED_COUPON_EXCEPTION = "아직 사용되지 않은 쿠폰입니다.";
}
