package com.firstone.greenjangteo.coupon.repository;

import com.firstone.greenjangteo.coupon.model.entity.Coupon;
import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class CouponRepositoryTest {
    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("쿠폰 그룹과 사용자가 비어 있는 조건을 통해 일정 수량의 쿠폰 목록을 조회할 수 있다.")
    @Test
    void findByCouponGroupAndUserIsNull() {
        // given
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        CouponGroup couponGroup = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, QUANTITY_TO_PROVIDE, tomorrow, EXPIRATION_PERIOD1
        );
        List<Coupon> createdCoupons = CouponTestObjectFactory.createCoupons(couponGroup);
        couponGroupRepository.save(couponGroup);
        couponRepository.saveAll(createdCoupons);

        int requiredQuantity = Integer.parseInt(QUANTITY_TO_PROVIDE);
        Pageable limit = PageRequest.of(0, requiredQuantity);

        // when
        List<Coupon> foundCoupons = couponRepository.findByCouponGroupAndUserIsNull(couponGroup, limit);

        // then
        assertThat(foundCoupons).hasSize(requiredQuantity)
                .extracting("user")
                .containsNull();
    }
}
