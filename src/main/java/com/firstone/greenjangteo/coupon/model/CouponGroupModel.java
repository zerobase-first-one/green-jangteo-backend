package com.firstone.greenjangteo.coupon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class CouponGroupModel {
    private String couponName;
    private String amount;
    private String description;
    private String issueQuantity;
    private LocalDateTime scheduledIssueDate;
    private String expirationPeriod;
}
