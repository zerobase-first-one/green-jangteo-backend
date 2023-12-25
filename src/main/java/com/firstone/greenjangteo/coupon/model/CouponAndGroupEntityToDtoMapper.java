package com.firstone.greenjangteo.coupon.model;

import com.firstone.greenjangteo.coupon.dto.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;

public class CouponAndGroupEntityToDtoMapper {
    public static IssueCouponsRequestDto toIssueCouponsRequestDto(CouponGroup couponGroup, int requiredQuantity) {
        String amount = String.valueOf(couponGroup.getAmount().getValue());
        String issueQuantity = String.valueOf(requiredQuantity);
        String expirationPeriod = String.valueOf(couponGroup.getExpirationPeriod().getValue());

        return IssueCouponsRequestDto.builder()
                .couponName(couponGroup.getCouponName())
                .amount(amount)
                .description(couponGroup.getDescription())
                .issueQuantity(issueQuantity)
                .scheduledIssueDate(couponGroup.getScheduledIssueDate())
                .expirationPeriod(expirationPeriod)
                .build();
    }
}
