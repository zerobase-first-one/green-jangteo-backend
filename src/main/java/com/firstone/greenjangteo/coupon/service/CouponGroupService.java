package com.firstone.greenjangteo.coupon.service;

import com.firstone.greenjangteo.coupon.dto.request.IssueCouponsRequestDto;
import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponGroupService {
    void addCouponGroupToImmediatelyIssue(IssueCouponsRequestDto issueCouponsRequestDto);

    List<CouponGroup> getCouponGroups();

    Page<Coupon> getCouponGroup(Long couponGroupId, Pageable pageable);

    CouponGroup getCouponGroup(String couponName);

    void deleteCouponGroup(Long couponGroupId);
}
