package com.firstone.greenjangteo.coupon.repository;

import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByCouponGroupAndUserIsNull(CouponGroup couponGroup, Pageable pageable);
}
