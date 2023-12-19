package com.firstone.greenjangteo.coupon.repository;

import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
