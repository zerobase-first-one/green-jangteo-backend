package com.firstone.greenjangteo.coupon.repository;

import com.firstone.greenjangteo.coupon.model.entity.CouponGroup;
import com.firstone.greenjangteo.coupon.testutil.CouponTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.firstone.greenjangteo.coupon.testutil.CouponTestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CouponGroupRepositoryTest {
    @Autowired
    private CouponGroupRepository couponGroupRepository;

    LocalDate tomorrow = LocalDate.now().plusDays(1);

    @DisplayName("쿠폰 이름을 통해 쿠폰 그룹을 찾을 수 있다.")
    @Test
    void findByCouponName() {
        // given
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
        CouponGroup createdCouponGroup
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );
        couponGroupRepository.save(createdCouponGroup);

        Optional<CouponGroup> foundCouponGroup = couponGroupRepository.findByCouponName(COUPON_NAME2);

        // when, then
        assertThat(foundCouponGroup).isNotPresent();
    }

    @DisplayName("scheduledIssueDate를 통해 해당 날짜에 발행 예정인 쿠폰 그룹을 찾을 수 있다.")
    @Test
    void findByScheduledIssueDate() {
        // given
        CouponGroup createdCouponGroup1
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME1, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );
        CouponGroup createdCouponGroup2
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME2, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow.plusDays(1), EXPIRATION_PERIOD
        );
        CouponGroup createdCouponGroup3
                = CouponTestObjectFactory.createCouponGroup(
                COUPON_NAME3, AMOUNT, DESCRIPTION, ISSUE_QUANTITY, tomorrow, EXPIRATION_PERIOD
        );
        couponGroupRepository.saveAll(List.of(createdCouponGroup1, createdCouponGroup2, createdCouponGroup3));

        List<CouponGroup> foundCouponGroups = couponGroupRepository.findByScheduledIssueDate(tomorrow);

        // when, then
        assertThat(foundCouponGroups).hasSize(2)
                .extracting("couponName", "issueQuantity")
                .containsExactlyInAnyOrder(
                        tuple(createdCouponGroup1.getCouponName(), createdCouponGroup1.getIssueQuantity()),
                        tuple(createdCouponGroup3.getCouponName(), createdCouponGroup2.getIssueQuantity())
                );
    }
}
