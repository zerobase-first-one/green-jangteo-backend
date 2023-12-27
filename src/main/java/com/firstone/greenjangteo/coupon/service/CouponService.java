package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUserRequestDto;
import com.firstone.greenjangteo.coupon.dto.request.ProvideCouponsToUsersRequestDto;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    void createCoupons(IssueCouponsRequestDto issueCouponsRequestDto) throws JobExecutionException;

    void issueCoupons() throws JobExecutionException;

    void provideCouponsToUsers(ProvideCouponsToUsersRequestDto provideCouponsToUsersRequestDto)
            throws JobExecutionException;

    void deleteExpiredCoupons() throws JobExecutionException;

    void provideCouponsToUser(ProvideCouponsToUserRequestDto provideCouponsToUserRequestDto);

    Page<Coupon> getCouponGroup(Long couponGroupId, Pageable pageable);

    CouponGroup getCouponGroup(String couponName);
}
