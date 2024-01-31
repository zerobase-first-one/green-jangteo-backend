package com.firstone.greenjangteo.order.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    BEFORE_PAYMENT("결제 전"),
    PAID("결제 완료"),
    CANCELED("주문 취소"),
    REFUSED("주문 거절"),
    REFUNDED("환불 완료"),
    IN_PREPARATION("상품 준비 중"),
    IN_DELIVERY("배송 중"),
    DELIVERED("배송 완료"),
    REVIEWED("리뷰 완료");

    private final String description;
}
