package com.firstone.greenjangteo.coupon.repository;

import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponGroupRepository extends JpaRepository<CouponGroup, Long> {
    Optional<CouponGroup> findByCouponName(String couponName);

    List<CouponGroup> findByScheduledIssueDate(LocalDate scheduledIssueDate);
}
