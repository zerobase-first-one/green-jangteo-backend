package com.firstone.greenjangteo.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class CouponGroupResponseDto {
    private Long couponGroupId;
    private String couponName;
    private int amount;
    private String description;
    private int issuedQuantity;
    private int remainingQuantity;
    private LocalDate scheduledIssueDate;
    private int expirationPeriod;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CouponResponseDto> couponResponseDtos;
}
