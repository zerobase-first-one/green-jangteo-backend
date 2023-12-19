package com.firstone.greenjangteo.coupon.model.entity;

import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CouponTest {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);
    LocalDateTime now = LocalDateTime.now();

    @DisplayName("쿠폰을 생성할 수 있다.")
    @Test
    void create() {
        // given
        CouponGroup couponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );

        // when
        Coupon coupon = new Coupon(couponGroup, now);

        // then
        assertThat(coupon.getCouponGroup()).isEqualTo(couponGroup);
        assertThat(coupon.getCreatedAt()).isEqualTo(now);
    }

    @DisplayName("동일한 내부 값들을 전송하면 동등한 Coupon 인스턴스를 생성한다.")
    @Test
    void ofSameValue() {
        // given
        CouponGroup couponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );

        // when
        Coupon coupon1 = new Coupon(couponGroup, now);
        Coupon coupon2 = new Coupon(couponGroup, now);

        // then
        assertThat(coupon1).isEqualTo(coupon2);
        assertThat(coupon1.hashCode()).isEqualTo(coupon2.hashCode());
    }

    @DisplayName("다른 내부 값들을 전송하면 동등하지 않은 Coupon 인스턴스를 생성한다.")
    @Test
    void ofDifferentValue() {
        // given
        CouponGroup couponGroup1
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );

        CouponGroup couponGroup2
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, dayAfterTomorrow, EXPIRATION_PERIOD
        );

        // when
        Coupon coupon1 = new Coupon(couponGroup1, now);
        Coupon coupon2 = new Coupon(couponGroup2, now);

        // then
        assertThat(coupon1).isNotEqualTo(coupon2);
        assertThat(coupon1.hashCode()).isNotEqualTo(coupon2.hashCode());
    }
}