package com.firstone.greenjangteo.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Builder
@Getter
public class CouponGroupModel {
    private String couponName;
    private String amount;
    private String description;
    private String issueQuantity;
    private LocalDate scheduledIssueDate;
    private String expirationPeriod;
}
