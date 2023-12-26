package com.firstone.greenjangteo.coupon.testutil;

import com.firstone.greenjangteo.application.model.CouponGroupModel;
import com.firstone.greenjangteo.coupon.dto.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.model.Amount;
import com.firstone.greenjangteo.coupon.model.ExpirationPeriod;
import com.firstone.greenjangteo.coupon.model.IssueQuantity;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CouponTestObjectFactory {
    public static IssueCouponsRequestDto createIssueCouponsRequestDto(
            String couponName, String amount, String description, String issueQuantity,
            LocalDate scheduledIssueDate, String expirationPeriod
    ) {
        return IssueCouponsRequestDto.builder()
                .couponName(couponName)
                .amount(amount)
                .description(description)
                .issueQuantity(issueQuantity)
                .scheduledIssueDate(scheduledIssueDate)
                .expirationPeriod(expirationPeriod)
                .build();
    }

    public static CouponGroupModel createCouponGroupModel(
            String couponName, String amount, String description, String issueQuantity,
            LocalDate scheduledIssueDate, String expirationPeriod
    ) {
        return CouponGroupModel.builder()
                .couponName(couponName)
                .amount(amount)
                .description(description)
                .issueQuantity(issueQuantity)
                .scheduledIssueDate(scheduledIssueDate)
                .expirationPeriod(expirationPeriod)
                .build();
    }

    public static CouponGroup createCouponGroup(
            String couponName, String amount, String description, String issueQuantity,
            LocalDate tomorrow, String expirationPeriod
    ) {
        return CouponGroup.builder()
                .couponName(couponName)
                .amount(Amount.of(amount))
                .description(description)
                .issueQuantity(IssueQuantity.of(issueQuantity))
                .scheduledIssueDate(tomorrow)
                .expirationPeriod(ExpirationPeriod.of(expirationPeriod))
                .build();
    }

    public static List<Coupon> createCoupons(CouponGroup couponGroup) {
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 0; i < couponGroup.getIssueQuantity().getValue(); i++) {
            coupons.add(new Coupon(couponGroup, LocalDateTime.now()));
        }

        return coupons;
    }

    public static List<Coupon> createAndIssueCoupons(CouponGroup couponGroup, LocalDateTime expirationTime) {
        List<Coupon> coupons = createCoupons(couponGroup);

        for (Coupon coupon : coupons) {
            coupon.issueCoupon(LocalDateTime.now(), expirationTime);
        }

        return coupons;
    }
}
