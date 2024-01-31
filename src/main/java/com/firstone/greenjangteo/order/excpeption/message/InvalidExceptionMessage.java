package com.firstone.greenjangteo.order.excpeption.message;

public class InvalidExceptionMessage {
    public static final String INVALID_ORDER_QUANTITY_EXCEPTION
            = "주문 수량은 양의 정수(1 이상의 숫자)여야 합니다. 입력된 주문 수량: ";
    public static final String INVALID_ORDER_PRICE_EXCEPTION
            = "쿠폰과 적립금 적용 후 주문 가격은 양의 정수(1 이상의 숫자)여야 합니다. 현재 주문 가격: ";
}
