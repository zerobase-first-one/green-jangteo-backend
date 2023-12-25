package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.ProvideCouponsToUserRequestDto;
import com.firstone.greenjangteo.coupon.dto.ProvideCouponsToUsersRequestDto;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import org.springframework.batch.core.JobExecutionException;

public interface CouponService {
    void createCoupons(IssueCouponsRequestDto issueCouponsRequestDto) throws JobExecutionException;

    void issueCoupons() throws JobExecutionException;

    void provideCouponsToUsers(ProvideCouponsToUsersRequestDto provideCouponsToUsersRequestDto)
            throws JobExecutionException;

    void deleteExpiredCoupons() throws JobExecutionException;

    void provideCouponsToUser(ProvideCouponsToUserRequestDto provideCouponsToUserRequestDto);

    CouponGroup getCouponGroup(String couponName);
}
