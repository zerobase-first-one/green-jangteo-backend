package com.firstone.greenjangteo.coupon.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class CouponResponseDto {
    private Long couponId;
    private Long couponGroupId;
    private String couponName;
    private int amount;
    private String description;
    private Long userId;
    private Long usedOrderId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime issuedAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expiredAt;

    public static CouponResponseDto toAdmin(Coupon coupon) {
        User user = coupon.getUser();
        Long userId = user == null ? null : user.getId();

        return CouponResponseDto.builder()
                .couponId(coupon.getId())
                .createdAt(coupon.getCreatedAt())
                .modifiedAt(coupon.getModifiedAt())
                .issuedAt(coupon.getIssuedAt())
                .expiredAt(coupon.getExpiredAt())
                .userId(userId)
                .usedOrderId(coupon.getUsedOrderId())
                .build();
    }

    public static CouponResponseDto toPrincipal(Coupon coupon) {
        CouponGroup couponGroup = coupon.getCouponGroup();
        User user = coupon.getUser();
        Long userId = user == null ? null : user.getId();

        return CouponResponseDto.builder()
                .couponId(coupon.getId())
                .couponGroupId(couponGroup.getId())
                .couponName(couponGroup.getCouponName())
                .amount(couponGroup.getAmount().getValue())
                .description(couponGroup.getDescription())
                .createdAt(coupon.getCreatedAt())
                .modifiedAt(coupon.getModifiedAt())
                .issuedAt(coupon.getIssuedAt())
                .expiredAt(coupon.getExpiredAt())
                .userId(userId)
                .usedOrderId(coupon.getUsedOrderId())
                .build();
    }
}
