package com.firstone.greenjangteo.coupon.repository;

import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CouponGroupRepositoryTest {
    @Autowired
    private CouponGroupRepository couponGroupRepository;

    @DisplayName("쿠폰 이름을 통해 쿠폰 그룹을 찾을 수 있다.")
    @Test
    void findByCouponName() {
        // given
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1)
                .truncatedTo(ChronoUnit.MILLIS);

        CouponGroup createdCouponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );
        couponGroupRepository.save(createdCouponGroup);

        // when
        CouponGroup foundCouponGroup = couponGroupRepository.findByCouponName(COUPON_NAME1).get();

        // then
        assertThat(foundCouponGroup.getCouponName()).isEqualTo(createdCouponGroup.getCouponName());
        assertThat(foundCouponGroup.getAmount()).isEqualTo(createdCouponGroup.getAmount());
        assertThat(foundCouponGroup.getDescription()).isEqualTo(createdCouponGroup.getDescription());
        assertThat(foundCouponGroup.getIssueQuantity()).isEqualTo(createdCouponGroup.getIssueQuantity());
        assertThat(foundCouponGroup.getScheduledIssueDate()).isEqualTo(createdCouponGroup.getScheduledIssueDate());
        assertThat(foundCouponGroup.getExpirationPeriod()).isEqualTo(createdCouponGroup.getExpirationPeriod());
    }

    @DisplayName("존재하지 않는 쿠폰 이름을 통해 쿠폰을 찾으려 하면 쿠폰 그룹을 반환하지 않는다.")
    @Test
    void findByNonExistentCouponName() {
        // given
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1)
                .truncatedTo(ChronoUnit.MILLIS);

        CouponGroup createdCouponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );
        couponGroupRepository.save(createdCouponGroup);

        Optional<CouponGroup> foundCouponGroup = couponGroupRepository.findByCouponName(COUPON_NAME2);

        // when, then
        assertThat(foundCouponGroup).isNotPresent();
    }
}