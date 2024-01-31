package com.firstone.greenjangteo.coupon.dto.request;

import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProvideCouponsToUserRequestDto {
    private User user;
    private String couponName;
    private int quantity;
}
