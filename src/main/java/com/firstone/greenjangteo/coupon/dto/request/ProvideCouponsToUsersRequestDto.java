package com.firstone.greenjangteo.coupon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ProvideCouponsToUsersRequestDto {
    private Long couponGroupId;
    List<Long> userIds;
    private int quantity;
}
