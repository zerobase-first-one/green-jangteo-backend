package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.IssueCouponsRequestDto;
import org.springframework.batch.core.JobExecutionException;

public interface CouponService {
    void createCoupons(IssueCouponsRequestDto issueCouponsRequestDto) throws JobExecutionException;
}
