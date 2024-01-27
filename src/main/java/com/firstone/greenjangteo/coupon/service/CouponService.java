package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUserRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUsersRequestDto;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import org.springframework.batch.core.JobExecutionException;

import java.util.List;

public interface CouponService {
    void createCoupons(IssueCouponsRequestDto issueCouponsRequestDto) throws JobExecutionException;

    void issueCoupons() throws JobExecutionException;

    void provideCouponsToUsers(ProvideCouponsToUsersRequestDto provideCouponsToUsersRequestDto)
            throws JobExecutionException;

    void deleteExpiredCoupons() throws JobExecutionException;

    void provideCouponsToUser(ProvideCouponsToUserRequestDto provideCouponsToUserRequestDto);

    List<Coupon> getCoupons(Long userId);

    int updateUsedCoupon(Long orderId, Long couponId);

    void deleteCoupon(long couponId);
}

