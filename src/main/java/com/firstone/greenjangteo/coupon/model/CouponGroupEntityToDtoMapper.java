package com.firstone.greenjangteo.coupon.model;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.response.CouponGroupResponseDto;
import com.firstone.greenjangteo.coupon.dto.response.CouponResponseDto;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class CouponGroupEntityToDtoMapper {
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

    public static CouponGroupResponseDto toCouponGroupResponseDto(Page<Coupon> couponPage) {
        if (couponPage == null || couponPage.isEmpty()) {
            return null;
        }

        CouponGroup couponGroup = couponPage.getContent().get(0).getCouponGroup();
        int couponSize = couponGroup.getCoupons() == null ? 0 : couponGroup.getCoupons().size();

        return CouponGroupResponseDto.builder()
                .couponGroupId(couponGroup.getId())
                .couponName(couponGroup.getCouponName())
                .amount(couponGroup.getAmount().getValue())
                .description(couponGroup.getDescription())
                .issuedQuantity(couponSize)
                .remainingQuantity(couponGroup.getRemainingQuantity())
                .scheduledIssueDate(couponGroup.getScheduledIssueDate())
                .expirationPeriod(couponGroup.getExpirationPeriod().getValue())
                .createdAt(couponGroup.getCreatedAt())
                .modifiedAt(couponGroup.getModifiedAt())
                .couponResponseDtos(toCouponResponseDtos(couponPage.getContent()))
                .build();
    }

    private static List<CouponResponseDto> toCouponResponseDtos(List<Coupon> coupons) {
        List<CouponResponseDto> couponResponseDtos = new ArrayList<>();
        for (Coupon coupon : coupons) {
            couponResponseDtos.add(CouponResponseDto.from(coupon));
        }

        return couponResponseDtos;
    }
}
